package com.beauleeful.demorestapi.events;


import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;


public class EventTest {

    /**
     * 실행 - control + shift + r
     * 불필요한 임포트 제가 = control + option + o
     * import - option + enter + import static method
     */

    @Test
    public void builder(){
        Event event = Event.builder()
                .name("REST API")
                .description("REST API development with Spring")
                .build();
        assertThat(event).isNotNull();
    }

    /**
     * builder annotation을 설저하면 디폴트 생성자 안만들어 지고 private 으로 만들어짐
     */
    @Test
    public void javaBean(){
        // Given
        String name = "Event";
        String description = "Spring";

        // When
        Event event = new Event();
        event.setName(name);
        event.setDescription("Spring");

        // Then
        assertThat(event.getName()).isEqualTo(name);
        assertThat(event.getDescription()).isEqualTo(description);
    }
}