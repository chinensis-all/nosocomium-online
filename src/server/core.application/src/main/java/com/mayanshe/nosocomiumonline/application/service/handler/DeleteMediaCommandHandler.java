package com.mayanshe.nosocomiumonline.application.service.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mayanshe.nosocomiumonline.application.dto.command.DeleteMediaCommand;
import com.mayanshe.nosocomiumonline.domain.kernel.eventing.OutboxRepository;
import com.mayanshe.nosocomiumonline.domain.media.model.Media;
import com.mayanshe.nosocomiumonline.domain.media.repository.MediaRepository;
import com.mayanshe.nosocomiumonline.shared.cqrs.CommandHandler;
import com.mayanshe.nosocomiumonline.shared.event.DomainEventCollector;
import com.mayanshe.nosocomiumonline.shared.exception.NotFoundException;
import com.mayanshe.nosocomiumonline.shared.storage.ObjectStorageService;
import com.mayanshe.nosocomiumonline.shared.event.DomainEventPublisher;
import com.mayanshe.nosocomiumonline.domain.media.event.MediaDeletedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/** * DeleteMediaCommandHandler: 处理删除媒体命令
 *
 * @author zhangxihai
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DeleteMediaCommandHandler implements CommandHandler<DeleteMediaCommand, Void> {
    private final OutboxRepository outboxRepository;

    private final MediaRepository mediaRepository;

    private final ObjectStorageService objectStorageService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Void handle(DeleteMediaCommand command) {
        Media media = mediaRepository.load(command.id()).orElseThrow(() -> new NotFoundException("媒体资源不存在: " + command.id()));

        // 删除数据库记录
        media.delete();
        mediaRepository.destroy(command.id());

        // 删除 COS 对象 (如果数据库删除成功)
        try {
            objectStorageService.delete(media.getBucketType(), media.getObjectKey());
        } catch (Exception e) {
            log.error("Failed to delete object from storage. key={}", media.getObjectKey(), e);
        }

        // 记录领域事件
        outboxRepository.save(DomainEventCollector.drain(), Media.class.getSimpleName(), String.valueOf(media.getId().getId()));

        return null;
    }
}
