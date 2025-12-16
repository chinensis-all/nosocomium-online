package com.mayanshe.nosocomiumonline.application.service.handler;

import com.mayanshe.nosocomiumonline.application.dto.command.DeleteMediaCommand;
import com.mayanshe.nosocomiumonline.domain.media.Media;
import com.mayanshe.nosocomiumonline.domain.media.MediaRepository;
import com.mayanshe.nosocomiumonline.shared.cqrs.CommandHandler;
import com.mayanshe.nosocomiumonline.shared.exception.NotFoundException;
import com.mayanshe.nosocomiumonline.shared.storage.ObjectStorageService;
import com.mayanshe.nosocomiumonline.shared.event.DomainEventPublisher;
import com.mayanshe.nosocomiumonline.domain.media.event.MediaDeletedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class DeleteMediaCommandHandler implements CommandHandler<DeleteMediaCommand, Void> {

    private final MediaRepository mediaRepository;
    private final ObjectStorageService objectStorageService;
    private final DomainEventPublisher domainEventPublisher;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Void handle(DeleteMediaCommand command) {
        Media media = mediaRepository.findById(command.getId())
                .orElseThrow(() -> new NotFoundException("文件不存在"));

        // 1. 删除数据库记录
        mediaRepository.deleteById(command.getId());

        // 2. 删除 COS 对象 (如果数据库删除成功)
        try {
            objectStorageService.delete(media.getBucketType(), media.getObjectKey());
        } catch (Exception e) {
            log.error("Failed to delete object from storage. key={}", media.getObjectKey(), e);
            // 记录日志但不回滚事务？或者让它抛出异常回滚？
            // 这里的策略是：优先保证数据库一致性。如果对象存储删除失败，产生垃圾数据，可以通过后台任务清理。
            // 但如果回滚了数据库删除，用户会觉得删除失败。
            // 权衡之下，这里只是记录 Error 日志，不阻断流程。
        }

        // 3. 发布领域事件
        domainEventPublisher.publish(new MediaDeletedEvent(command.getId()));

        return null;
    }
}
