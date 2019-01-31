package com.beauleeful.demorestapi.events;

import com.beauleeful.demorestapi.common.ErrorsResource;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.util.Optional;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

@Controller
@RequestMapping(value = "/api/events", produces = MediaTypes.HAL_JSON_UTF8_VALUE) //all handler response HAL_JSON_UTF8
public class EventController {

    //@Autowired
    private final EventRepository eventRepository;

    private final ModelMapper modelMapper;

    private final EventValidator eventValidator;

    public EventController(EventRepository eventRepository, ModelMapper modelMapper, EventValidator eventValidator){
        this.eventRepository = eventRepository;
        this.modelMapper = modelMapper;
        this.eventValidator = eventValidator;
    }

    @PostMapping
    public ResponseEntity createEvent(@RequestBody @Valid EventDto eventDto, Errors errors){
        if (errors.hasErrors()){
            //return ResponseEntity.badRequest().body(errors);
            return badRequest(errors);
        }

        eventValidator.validate(eventDto, errors);
        if (errors.hasErrors()){
            return badRequest(errors);
        }

        Event event = modelMapper.map(eventDto,Event.class);
        event.update();
        Event newEvent = this.eventRepository.save(event);
        ControllerLinkBuilder selfLinkBuilder = linkTo(EventController.class).slash(newEvent.getId());
        URI createUri = selfLinkBuilder.toUri();
        EventResource eventResource = new EventResource(event);
        eventResource.add(linkTo(EventController.class).withRel("query-events"));
        //eventResource.add(selfLinkBuilder.withSelfRel()); //self link add to resource
        eventResource.add(selfLinkBuilder.withRel("update-event"));
        eventResource.add(new Link("/docs.index.html#resources-events-create").withRel("profile"));
        return ResponseEntity.created(createUri).body(eventResource);
    }

    @GetMapping
    public ResponseEntity queryEvents(Pageable pageable, PagedResourcesAssembler<Event> assembler) {
        Page<Event> page = this.eventRepository.findAll(pageable); //option + Enter + v : extract variable
        var pageResources = assembler.toResource(page, e -> new EventResource(e));
        pageResources.add(new Link("/docs.index.html#resources-events-list").withRel("profile"));
        return ResponseEntity.ok(pageResources);
    }

    @GetMapping("/{id}")
    public ResponseEntity getEvent(@PathVariable Integer id) {
        Optional<Event> optionalEvent = this.eventRepository.findById(id);
        if (optionalEvent.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Event event = optionalEvent.get();
        EventResource eventResource = new EventResource(event);
        eventResource.add(new Link("/docs.index.html#resources-events-get").withRel("profile"));
        return ResponseEntity.ok(eventResource);
    }

    @PutMapping("/{id}")
    public ResponseEntity updateEvent(@PathVariable Integer id,
                                      @RequestBody @Valid EventDto eventDto,
                                      Errors errors){

        Optional<Event> optionalEvent = this.eventRepository.findById(id);
        if(optionalEvent.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        if (errors.hasErrors()) { //dto annotation error
            return badRequest(errors);
        }

        this.eventValidator.validate(eventDto,errors);
        if (errors.hasErrors()) { // logic error
            return badRequest(errors);
        }

        Event existingEvent = optionalEvent.get();
        this.modelMapper.map(eventDto , existingEvent);
        Event savedEvent = this.eventRepository.save(existingEvent);

        EventResource eventResource = new EventResource(savedEvent);
        eventResource.add(new Link("/docs.index.html#resources-events-update").withRel("profile"));
        return ResponseEntity.ok(eventResource);
    }

    private ResponseEntity badRequest(Errors errors) {
        return ResponseEntity.badRequest().body(new ErrorsResource(errors));
    }
}
