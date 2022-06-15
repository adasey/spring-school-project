package iducs.springboot.lmoboard.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;

@Service
@Slf4j
public class SessionOutService implements ApplicationListener<ContextClosedEvent> {
    private HttpSession session;

    @Override
    public void onApplicationEvent(ContextClosedEvent event) {
        log.info("session get closed {}", event);
//        try {
//            sessionInval();
//        } catch(Exception e) {
//            log.info("no session found : {}", e);
//        }
    }

    public void setSession(HttpSession session) {
        this.session = session;
    }

    void sessionInval() {
        session.invalidate();
    }
}
