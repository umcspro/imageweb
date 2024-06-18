package com.umcspro.imageweb;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
public class RectangleController {
    private List<Rectangle> rectangles = new ArrayList<>();
    @GetMapping("rectangle")
    public Rectangle getRectangle() {
        return new Rectangle(10, 10, 100, 100, "red");
    }

    @PostMapping("/addRectangle")
    public ResponseEntity<Void> addRectangle(@RequestBody Rectangle rectangle) {
        rectangles.add(rectangle);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/rectangles")
    public ResponseEntity<List<Rectangle>> getRectangles() {
        return ResponseEntity.ok(rectangles);
    }

    @GetMapping("/rectangles/svg")
    public ResponseEntity<String> getRectanglesSvg() {
        StringBuilder svg = new StringBuilder();
        svg.append("<svg width=\"800\" height=\"600\" xmlns=\"http://www.w3.org/2000/svg\">");
        for (Rectangle rectangle : rectangles) {
            svg.append("<rect x=\"")
                    .append(rectangle.getX())
                    .append("\" y=\"")
                    .append(rectangle.getY())
                    .append("\" width=\"")
                    .append(rectangle.getWidth())
                    .append("\" height=\"")
                    .append(rectangle.getHeight())
                    .append("\" fill=\"")
                    .append(rectangle.getColor())
                    .append("\" />");
        }
        svg.append("</svg>");
        return ResponseEntity.ok(svg.toString());
    }

    @GetMapping("/rectangles/{index}")
    public ResponseEntity<Rectangle> getRectangleByIndex(@PathVariable int index) {
        if (index < 0 || index >= rectangles.size()) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(rectangles.get(index));
    }

    @PutMapping("/rectangles/{index}")
    public ResponseEntity<Void> updateRectangle(@PathVariable int index, @RequestBody Rectangle rectangle) {
        if (index < 0 || index >= rectangles.size()) {
            return ResponseEntity.badRequest().build();
        }
        rectangles.set(index, rectangle);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/rectangles/{index}")
    public ResponseEntity<Void> deleteRectangle(@PathVariable int index) {
        if (index < 0 || index >= rectangles.size()) {
            return ResponseEntity.badRequest().build();
        }
        rectangles.remove(index);
        return ResponseEntity.ok().build();
    }
}

