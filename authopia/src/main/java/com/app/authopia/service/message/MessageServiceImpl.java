package com.app.authopia.service.message;

import com.app.authopia.dao.FileDAO;
import com.app.authopia.dao.MessageDAO;
import com.app.authopia.domain.dto.MessageDTO;
import com.app.authopia.domain.dto.PaginationMessage;
import com.app.authopia.domain.type.FileType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessageServiceImpl implements MessageService {
    private final MessageDAO messageDAO;
    private final FileDAO fileDAO;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<MessageDTO> getReceiveList(PaginationMessage pagination, Long memberId, String keyword) {
        final List<MessageDTO> datas = messageDAO.findReceiveAll(pagination, memberId, keyword);
        datas.forEach(data -> data.setMessageFiles(fileDAO.findAllMessageFile(data.getId())));
//        datas.forEach(data -> data.setMemberProfileImage(fileDAO.findProfileImage(data.getSendMemberId()).isPresent() ? fileDAO.findProfileImage(data.getSendMemberId()).get() : null));
        return datas;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<MessageDTO> getSendList(PaginationMessage pagination, Long memberId, String keyword) {
        final List<MessageDTO> datas = messageDAO.findSendAll(pagination, memberId, keyword);
        datas.forEach(data -> data.setMessageFiles(fileDAO.findAllMessageFile(data.getId())));
//        datas.forEach(data -> data.setMemberProfileImage(fileDAO.findProfileImage(data.getReceiveMemberId()).isPresent() ? fileDAO.findProfileImage(data.getReceiveMemberId()).get() : null));
        return datas;
    }

    @Override
    public int getReceiveTotal(Long memberId, String keyword) {
        return messageDAO.findCountOfReceiveMessage(memberId, keyword);
    }

    @Override
    public int getSendTotal(Long memberId, String keyword) {
        return messageDAO.findCountOfSendMessage(memberId, keyword);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void write(MessageDTO messageDTO) {
        messageDAO.save(messageDTO);
        for(int i=0; i<messageDTO.getMessageFiles().size(); i++){
            log.info(String.valueOf(messageDTO.getId()));
            messageDTO.getMessageFiles().get(i).setMessageId(messageDTO.getId());
            messageDTO.getMessageFiles().get(i).setFileType(i == 0 ? FileType.REPRESENTATIVE.name() : FileType.NON_REPRESENTATIVE.name());
            log.info(messageDTO.getMessageFiles().toString());
            fileDAO.saveMessageFile(messageDTO.getMessageFiles().get(i));
        }
    }

    @Override
    public Long checkIdByEmail(String memberEmail) {
        return messageDAO.findIdByEmail(memberEmail);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Optional<MessageDTO> getReceive(Long id) {
        final Optional<MessageDTO> foundMessage = messageDAO.findReceiveById(id);
        if(foundMessage.isPresent()){
            foundMessage.get().setMessageFiles(fileDAO.findAllMessageFile(foundMessage.get().getId()));
            foundMessage.get().setMemberProfileImage(fileDAO.findProfileImage(foundMessage.get().getSendMemberId()).isPresent() ? fileDAO.findProfileImage(foundMessage.get().getSendMemberId()).get() : null);
        }
        return foundMessage;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Optional<MessageDTO> getSend(Long id) {
        final Optional<MessageDTO> foundMessage = messageDAO.findSendById(id);
        if(foundMessage.isPresent()){
            foundMessage.get().setMessageFiles(fileDAO.findAllMessageFile(foundMessage.get().getId()));
            foundMessage.get().setMemberProfileImage(fileDAO.findProfileImage(foundMessage.get().getReceiveMemberId()).isPresent() ? fileDAO.findProfileImage(foundMessage.get().getReceiveMemberId()).get() : null);
        }
        return foundMessage;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void remove(Long id) {
        fileDAO.deleteAllMessageFile(id);
        messageDAO.delete(id);
    }

    @Override
    public void modify(Long id) {
        messageDAO.modify(id);
    }

    @Override
    public int getAlarm(Long memberId) {
        return messageDAO.findCountOfReceiveMessageUnRead(memberId);
    }
}
