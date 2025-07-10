package gift.repository.member;

import static gift.service.member.MemberServiceImpl.sha256;

import gift.entity.member.Member;
import gift.entity.member.value.Role;
import java.time.LocalDateTime;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

@Component
public class AdminAccountInitializer {

    private final MemberRepository memberRepository;

    public AdminAccountInitializer(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @EventListener(ContextRefreshedEvent.class)
    public void createAdminAccountIfNotExists() {
        String adminEmail = "admin@email.com";
        String adminPassword = "admin123";

        if (memberRepository.findByEmail(adminEmail).isEmpty()) {
            Member admin = Member.of(
                    null,
                    adminEmail,
                    sha256(adminPassword),
                    String.valueOf(Role.ADMIN),
                    LocalDateTime.now()
            );

            try {
                memberRepository.register(admin);
                System.out.println("Admin 계정 생성 완료");
            } catch (DataIntegrityViolationException e) {
                System.out.println("Admin 계정이 이미 존재합니다.");
            }
        } else {
            System.out.println("Admin 계정이 이미 존재합니다.");
        }
    }

    @EventListener(ContextRefreshedEvent.class)
    public void createUserAccountIfNotExists() {
        String userEmail = "user@user.com";
        String userPassword = "user123";
        if (memberRepository.findByEmail(userEmail).isEmpty()) {
            Member user = Member.of(
                    null,
                    userEmail,
                    sha256(userPassword),
                    String.valueOf(Role.USER),
                    LocalDateTime.now()
            );
            try {
                memberRepository.register(user);
                System.out.println("Admin 계정 생성 완료");
            } catch (DataIntegrityViolationException e) {
                System.out.println("Admin 계정이 이미 존재합니다.");
            }
        } else {
            System.out.println("Admin 계정이 이미 존재합니다.");
        }
    }
}
