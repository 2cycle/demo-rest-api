package com.beauleeful.demorestapi.events;

import com.beauleeful.demorestapi.common.RestDocsConfiguration;
import com.beauleeful.demorestapi.common.TestDescription;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.headers.HeaderDocumentation.responseHeaders;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@Import(RestDocsConfiguration.class)
@ActiveProfiles("test")
public class EventControllerTests {

    @Autowired //slicing annotation, no webserver
    MockMvc mockMvc;

    //json
    @Autowired
    ObjectMapper objectMapper;
//
//    @MockBean
//    EventRepository eventRepository;

    @Test
    @TestDescription("정상적으로 이벤트를 생성하는 테스트")
    public void createEvent() throws Exception {
        EventDto event = EventDto.builder()
                .name("Spring")
                .description("REST API Development with Spring")
                .beginEnrollmentDateTime(LocalDateTime.of(2019,01,17,07,52))
                .closeEnrollmentDateTime(LocalDateTime.of(2019,01,18,07,52))
                .beginEventDateTime(LocalDateTime.of(2019,01,19,07,52))
                .endEventDateTime(LocalDateTime.of(2019,01,20,07,52))
                .basePrice(100)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("강남역 D2 스타텁 팩토리")
                .build();

        /**
         * Mock 객체는 리턴되는 값이 모두 null
         * stubbing으로 mock 객체의 행동을 알려주야함
         */
//        event.setId(10);
//        Mockito.when(eventRepository.save(event)).thenReturn(event);

        mockMvc.perform(post("/api/events/")
                    .contentType(MediaType.ALL.APPLICATION_JSON_UTF8)
                    .accept(MediaTypes.HAL_JSON)
                    .content(objectMapper.writeValueAsString(event)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").exists())
                //.andExpect(header().exists("Location"))
                .andExpect(header().exists(HttpHeaders.LOCATION))
                //.andExpect(header().string("Content-Type","application/hal+json;charset=UTF-8"))
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_UTF8_VALUE))
                .andExpect(jsonPath("id").value(Matchers.not(100)))
                .andExpect(jsonPath("free").value(false))
                .andExpect(jsonPath("offline").value(true))
                .andExpect(jsonPath("eventStatus").value(EventStatus.DRAFT.name()))
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.query-events").exists())
                .andExpect(jsonPath("_links.update-event").exists())
                .andDo(document("create-event",
                        links(linkWithRel("self").description("link to self"),
                                linkWithRel("query-events").description("link to query events"),
                                linkWithRel("update-event").description("link to update an existing event"),
                                linkWithRel("profile").description("link to update an existing event")
                                ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        requestFields(
                               fieldWithPath("name").description("Name of new event"),
                               fieldWithPath("description").description("description of new event"),
                               fieldWithPath("beginEnrollmentDateTime").description("date time of begin of new event"),
                               fieldWithPath("closeEnrollmentDateTime").description("date time of close of new event"),
                               fieldWithPath("beginEventDateTime").description("date time of begin of new event"),
                               fieldWithPath("endEventDateTime").description("date time of end of new event"),
                               fieldWithPath("location").description("location of new event"),
                               fieldWithPath("basePrice").description("basePrice of new event"),
                               fieldWithPath("maxPrice").description("maxPrice of new event"),
                               fieldWithPath("limitOfEnrollment").description("limit of new event")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.LOCATION).description("Location Header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("Content Type")
                        ),
                        relaxedResponseFields( //relaxed 접두어는 문서의 일부분만 확인할 수 있다. 현재 relaxed가 없으면 지금 link검증이 안되므로 에러가남
                                fieldWithPath("name").description("Name of new event"),
                                fieldWithPath("description").description("description of new event"),
                                fieldWithPath("beginEnrollmentDateTime").description("date time of begin of new event"),
                                fieldWithPath("closeEnrollmentDateTime").description("date time of close of new event"),
                                fieldWithPath("beginEventDateTime").description("date time of begin of new event"),
                                fieldWithPath("endEventDateTime").description("date time of end of new event"),
                                fieldWithPath("location").description("location of new event"),
                                fieldWithPath("basePrice").description("basePrice of new event"),
                                fieldWithPath("maxPrice").description("maxPrice of new event"),
                                fieldWithPath("limitOfEnrollment").description("limit of new event"),
                                fieldWithPath("free").description("it tells if this event is free or not"),
                                fieldWithPath("offline").description("it tells if this event is offline or not"),
                                fieldWithPath("eventStatus").description("event status"),
                                fieldWithPath("_links.self.href").description("link to self"),
                                fieldWithPath("_links.query-events.href").description("link to query event list"),
                                fieldWithPath("_links.update-event.href").description("link to update existing event"),
                                // 상기 Hal JSON에서 link확인 하였는데 또하는 불편함이 있다. link를 추가 검수하거나, relaxed를 사용해야하므로 restdocs에 수정이 필요
                                fieldWithPath("_links.profile.href").description("link to update an existing event")
                        )
                ))
        ;
    }


    @Test
    @TestDescription("입력 받을 수 없는 값을 사용한 경우에 에러가 발생하는 테스트")
    public void createEvent_Bad_Request() throws Exception {
        Event event = Event.builder()
                .id(100)
                .name("Spring")
                .description("REST API Development with Spring")
                .beginEnrollmentDateTime(LocalDateTime.of(2019,01,17,07,52))
                .closeEnrollmentDateTime(LocalDateTime.of(2019,01,18,07,52))
                .beginEventDateTime(LocalDateTime.of(2019,01,19,07,52))
                .endEventDateTime(LocalDateTime.of(2019,01,20,07,52))
                .basePrice(100)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("강남역 D2 스타텁 팩토리")
                .free(true)
                .offline(false)
                .eventStatus(EventStatus.PUBLISHED)
                .build();

        /**
         * Mock 객체는 리턴되는 값이 모두 null
         * stubbing으로 mock 객체의 행동을 알려주야함
         */
//        event.setId(10);
//        Mockito.when(eventRepository.save(event)).thenReturn(event);

        mockMvc.perform(post("/api/events")
                .contentType(MediaType.ALL.APPLICATION_JSON_UTF8)
                .accept(MediaTypes.HAL_JSON)
                .content(objectMapper.writeValueAsString(event)))
                .andDo(print())
                .andExpect(status().isBadRequest())
        ;
    }

    @Test
    public void createEent_Bad_Request_Empty_Input() throws Exception {
        EventDto eventDto = EventDto.builder().build();

        this.mockMvc.perform(post("/api/events")
                    .contentType(MediaType.APPLICATION_JSON_UTF8)
                    .content(this.objectMapper.writeValueAsString(eventDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @TestDescription("입력 값이 잘못된 경우에 에러가 발생하는 테스")
    public void createEent_Bad_Request_Wrong_Input() throws Exception {
        EventDto eventDto = EventDto.builder()
                .name("Spring")
                .description("REST API Development with Spring")
                .beginEnrollmentDateTime(LocalDateTime.of(2019,01,23,07,52))
                .closeEnrollmentDateTime(LocalDateTime.of(2019,01,22,07,52))
                .beginEventDateTime(LocalDateTime.of(2019,01,21,07,52)) // time issue
                .endEventDateTime(LocalDateTime.of(2019,01,20,07,52))
                .basePrice(10000)
                .maxPrice(200) //max issue
                .limitOfEnrollment(100)
                .location("강남역 D2 스타텁 팩토리").build();

        this.mockMvc.perform(post("/api/events")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(this.objectMapper.writeValueAsString(eventDto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("content[0].objectName").exists())
                //.andExpect(jsonPath("$[0].field").exists())   // 글로벌 겸용
                .andExpect(jsonPath("content[0].defaultMessage").exists())
                .andExpect(jsonPath("content[0].code").exists())
                //.andExpect(jsonPath("$[0].rejectedValue").exists()) // 이런 값들이 있기를 기대함 (글로별 겸용)
                .andExpect(jsonPath("_links.index").exists())
        ;
    }

}
