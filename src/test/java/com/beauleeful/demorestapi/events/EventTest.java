package com.beauleeful.demorestapi.events;


import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(JUnitParamsRunner.class)
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

    @Test
    /*@Parameters({
            "0, 0, true",
            "100, 0, false",
            "0, 100, false"
    })*/
    //@Parameters(method = "paramsForTestFree")
    @Parameters
    public void testFree(int basePrice, int maxPrice, boolean isFree){
        // Given
        Event event = Event.builder()
                .basePrice(basePrice)
                .maxPrice(maxPrice)
                .build();

        // When
        event.update();

        // Then
        assertThat(event.isFree()).isEqualTo(isFree);

        /*
        // Given
        event = Event.builder()
                .basePrice(100)
                .maxPrice(0)
                .build();

        // When
        event.update();

        // Then
        assertThat(event.isFree()).isFalse();

        // Given
        event = Event.builder()
                .basePrice(0)
                .maxPrice(100)
                .build();

        // When
        event.update();

        // Then
        assertThat(event.isFree()).isFalse();
        */
    }

    private Object[] parametersForTestFree() { // parametersFor가 prefix
        return new Object[] {
                new Object[] {0, 0, true},
                new Object[] {100, 0, false},
                new Object[] {0, 100, false},
                new Object[] {1000, 100, false}
        };
    }

    @Test
    @Parameters
    public void testOffline(String location, boolean isOffline) {
        Event event = Event.builder()
                .location(location)
                .build();

        // When
        event.update();

        assertThat(event.isOffline()).isEqualTo(isOffline);
    }

    private Object[] parametersForTestOffline() { // parametersFor가 prefix
        return new Object[] {
                new Object[] {"강남", true},
                new Object[] {null, false},
                new Object[] {"       ", false}
        };

    }
}