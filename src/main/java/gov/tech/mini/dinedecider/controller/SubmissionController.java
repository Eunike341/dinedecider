package gov.tech.mini.dinedecider.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/submissions")
public class SubmissionController {

    @PostMapping("/{sessionUuid}")
    public void submitRestaurant () {

    }

    @GetMapping("/{sessionUuid}")
    public List viewRestaurant () {
        return null;
    }

    @GetMapping("/{sessionUuid}/decided")
    public void view () {

    }

}
