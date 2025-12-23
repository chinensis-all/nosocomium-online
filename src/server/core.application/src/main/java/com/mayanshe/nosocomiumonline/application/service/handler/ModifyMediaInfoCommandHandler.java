package com.mayanshe.nosocomiumonline.application.service.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mayanshe.nosocomiumonline.application.dto.MediaDto;
import com.mayanshe.nosocomiumonline.application.dto.MediaInfoDto;
import com.mayanshe.nosocomiumonline.application.dto.command.ModifyMediaInfoCommand;
import com.mayanshe.nosocomiumonline.domain.kernel.eventing.OutboxRepository;
import com.mayanshe.nosocomiumonline.domain.media.model.Media;
import com.mayanshe.nosocomiumonline.domain.media.repository.MediaRepository;
import com.mayanshe.nosocomiumonline.shared.cqrs.CommandHandler;
import com.mayanshe.nosocomiumonline.shared.event.DomainEventCollector;
import com.mayanshe.nosocomiumonline.shared.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * ModifyMediaInfoCommandHandler: 处理修改媒体信息命令
 *
 * @author zhangxihai
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ModifyMediaInfoCommandHandler implements CommandHandler<ModifyMediaInfoCommand, MediaInfoDto> {
    private final OutboxRepository outboxRepository;

    private final MediaRepository mediaRepository;

    private final ModelMapper modelMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MediaInfoDto handle(ModifyMediaInfoCommand command) {
        // 更新
        Media media = mediaRepository.load(command.id()).orElseThrow(() -> new NotFoundException("媒体资源不存在: " + command.id()));
        media.modifyInfo(command.title(), command.description());
        mediaRepository.save(media);

        // 保存领域事件
        outboxRepository.save(DomainEventCollector.drain(), Media.class.getSimpleName(), String.valueOf(media.getId().getId()));

        return new MediaInfoDto(
            media.getTitle(),
            media.getDescription()
        );
    }
}
