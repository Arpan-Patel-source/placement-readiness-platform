package com.majorproject.backend.coding;

import lombok.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TestCase {
    private String input;
    private String expectedOutput;
    private boolean hidden;
}
