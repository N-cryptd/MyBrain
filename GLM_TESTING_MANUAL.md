# GLM AI Integration - Manual Testing Plan

This document provides comprehensive manual testing scenarios for GLM AI integration verification.

## Overview

- **Total Test Scenarios**: 33
- **Test Categories**: Configuration Flow, Basic Chat, Tool Usage, Edge Cases, Error Scenarios, Performance
- **Estimated Testing Time**: 2-3 hours
- **Prerequisites**: Valid GLM API key, stable internet connection

---

## Configuration Flow Tests

### Test 1: Select GLM Provider in Settings
**Objective**: Verify GLM provider can be selected from settings

**Steps**:
1. Open Settings
2. Navigate to AI/LLM Settings
3. Select "GLM" from provider dropdown
4. Verify provider is selected

**Expected Result**:
- GLM option is visible
- Selection is persisted
- No crashes or errors

---

### Test 2: Enter Valid API Key
**Objective**: Verify valid API key can be saved

**Steps**:
1. Navigate to AI Settings
2. Enter valid GLM API key (e.g., `sk-xxxxxxxxxxxx`)
3. Save settings
4. Verify API key is masked in display (e.g., `sk-***...xxx`)

**Expected Result**:
- API key is saved
- Display shows masked version
- No validation errors

---

### Test 3: Verify Model Defaults to glm-4.7
**Objective**: Verify default model is set correctly

**Steps**:
1. Select GLM provider
2. Enter valid API key
3. Check model field
4. Verify default model name

**Expected Result**:
- Model field defaults to `glm-4.7`
- Model name is editable
- Default is correct

---

### Test 4: Test Custom URL Configuration
**Objective**: Verify custom GLM endpoint can be configured

**Steps**:
1. Enable "Use Custom URL" toggle
2. Enter custom GLM API endpoint (e.g., `https://open.bigmodel.cn/api/paas/v4`)
3. Save settings
4. Verify custom URL is saved

**Expected Result**:
- Toggle enables URL input field
- Custom URL is validated
- URL is persisted

---

### Test 5: Toggle Custom URL On/Off
**Objective**: Verify custom URL toggle works correctly

**Steps**:
1. Enable custom URL and enter value
2. Toggle custom URL OFF
3. Verify URL field is disabled
4. Toggle back ON
5. Verify URL value is preserved

**Expected Result**:
- Toggle enables/disables URL field
- URL value persists across toggle
- Default URL is restored when disabled

---

## Basic Chat Tests

### Test 6: Send Simple "Hello" Message
**Objective**: Verify basic chat functionality

**Steps**:
1. Configure GLM with valid API key
2. Open AI chat interface
3. Send "Hello"
4. Wait for response

**Expected Result**:
- Message is sent
- Response is received
- Response is coherent
- No errors

---

### Test 7: Verify Response is Received
**Objective**: Verify response display

**Steps**:
1. Send a question (e.g., "What is 2+2?")
2. Verify response appears
3. Check response formatting

**Expected Result**:
- Response appears in chat
- Formatting is correct
- Response is visible
- Response is complete (not truncated)

---

### Test 8: Check Streaming Works Smoothly
**Objective**: Verify streaming response functionality

**Steps**:
1. Ask a complex question (e.g., "Explain quantum computing")
2. Observe response generation
3. Verify text streams character by character

**Expected Result**:
- Response streams smoothly
- No stuttering or delays
- Text appears progressively
- Streaming completes without interruption

---

### Test 9: Test Multiple Messages in Conversation
**Objective**: Verify conversation context is maintained

**Steps**:
1. Send message: "My name is Alice"
2. Send message: "What is my name?"
3. Verify AI responds correctly

**Expected Result**:
- AI remembers previous message
- Response includes "Alice"
- Context is maintained across messages
- No context loss

---

## Tool Usage Tests

### Test 10: Ask AI to Create a Note
**Objective**: Verify note creation tool

**Steps**:
1. Ask: "Create a note called 'Shopping List' with 'Milk, Eggs, Bread'"
2. Verify tool call is initiated
3. Check note list

**Expected Result**:
- Tool is invoked
- Note is created with correct title
- Note contains correct content
- Confirmation message appears

---

### Test 11: Verify Note is Created
**Objective**: Verify note persistence

**Steps**:
1. Ask AI to create a note
2. Navigate to Notes section
3. Find the created note
4. Verify content

**Expected Result**:
- Note appears in Notes list
- Title matches request
- Content matches request
- Note is accessible

---

### Test 12: Ask AI to Create a Task
**Objective**: Verify task creation tool

**Steps**:
1. Ask: "Create a task called 'Finish report' with high priority due tomorrow at 5 PM"
2. Verify tool call
3. Check Tasks list

**Expected Result**:
- Tool is invoked
- Task is created
- Priority is set correctly
- Due date is set correctly

---

### Test 13: Verify Task is Created
**Objective**: Verify task persistence

**Steps**:
1. Ask AI to create a task
2. Navigate to Tasks section
3. Find the created task
4. Verify all details

**Expected Result**:
- Task appears in list
- Title is correct
- Priority matches
- Due date is correct

---

### Test 14: Ask AI to Search for Notes
**Objective**: Verify note search tool

**Steps**:
1. Create a test note with specific content
2. Ask: "Search for notes about [keyword]"
3. Verify search results

**Expected Result**:
- Search tool is called
- Relevant notes are found
- Results are displayed
- Correct notes are returned

---

### Test 15: Verify Search Results
**Objective**: Verify search accuracy

**Steps**:
1. Create multiple notes with different content
2. Ask AI to search for a specific term
3. Verify only matching notes appear

**Expected Result**:
- Only matching notes appear
- Search is case-insensitive (or follows expected behavior)
- All matching notes are included
- No false positives

---

### Test 16: Ask AI to Create Calendar Event
**Objective**: Verify calendar event creation

**Steps**:
1. Ask: "Create a meeting called 'Team Sync' tomorrow at 2 PM for 1 hour"
2. Verify tool call
3. Check calendar

**Expected Result**:
- Tool is invoked
- Event is created
- Title is correct
- Date/time is correct

---

### Test 17: Verify Event is Created
**Objective**: Verify event persistence

**Steps**:
1. Ask AI to create an event
2. Navigate to Calendar
3. Find the created event
4. Verify details

**Expected Result**:
- Event appears on calendar
- Title matches
- Date/time is accurate
- Event is accessible

---

### Test 18: Test Multiple Tool Calls in Single Request
**Objective**: Verify multiple tool execution

**Steps**:
1. Ask: "Create a note about the meeting and add a task to prepare for it"
2. Observe tool calls
3. Verify both are created

**Expected Result**:
- Both tools are called
- Note is created
- Task is created
- Both are persisted

---

## Edge Case Tests

### Test 19: Send Empty Message
**Objective**: Verify handling of empty input

**Steps**:
1. Open AI chat
2. Send empty message (press Enter with no text)
3. Verify behavior

**Expected Result**:
- Graceful handling
- No crash
- Appropriate error message or no action
- UI remains responsive

---

### Test 20: Test Very Long Message (1000+ chars)
**Objective**: Verify handling of large input

**Steps**:
1. Copy long text (1000+ characters)
2. Paste into chat input
3. Send message
4. Verify processing

**Expected Result**:
- Message is accepted
- No truncation
- Response is generated
- No performance issues

---

### Test 21: Test Rapid Consecutive Requests
**Objective**: Verify rate limit handling

**Steps**:
1. Send message A
2. Immediately send message B
3. Immediately send message C
4. Verify queue processing

**Expected Result**:
- Messages are processed sequentially
- No crashes
- All messages get responses
- Rate limiting works (if applicable)

---

### Test 22: Test with Special Characters in Message
**Objective**: Verify handling of special characters

**Steps**:
1. Send message with special chars: `Hello! @#$%^&*()_+-={}[]|\:;"'<>,.?/~`
2. Verify response

**Expected Result**:
- Message is sent correctly
- Special characters preserved
- No encoding issues
- Response is normal

---

### Test 23: Test with Emoji
**Objective**: Verify emoji handling

**Steps**:
1. Send message: "Hello! 😊🎉🚀"
2. Verify emoji display
3. Check response

**Expected Result**:
- Emojis are displayed correctly
- No rendering issues
- Response handles emoji
- No font issues

---

## Error Scenario Tests

### Test 24: Test with Invalid API Key
**Objective**: Verify error handling for invalid credentials

**Steps**:
1. Enter invalid API key (e.g., `invalid-key-123`)
2. Save settings
3. Send a message
4. Verify error handling

**Expected Result**:
- Clear error message shown
- No crash
- Error indicates authentication failure
- User can retry with correct key

---

### Test 25: Verify Clear Error Message Shown
**Objective**: Verify error message clarity

**Steps**:
1. Trigger an error (invalid API key, network issue, etc.)
2. Read error message
3. Verify clarity and actionable information

**Expected Result**:
- Error message is clear
- User understands what went wrong
- Suggested fix is provided
- Message is not cryptic

---

### Test 26: Test with No Internet Connection
**Objective**: Verify offline behavior

**Steps**:
1. Disable internet connection
2. Send a message
3. Verify error handling
4. Re-enable connection
5. Retry

**Expected Result**:
- Clear "no connection" error
- No crash
- Can retry after connection restored
- Previous context maintained

---

### Test 27: Verify Offline Error Handling
**Objective**: Verify offline state detection

**Steps**:
1. Disconnect from network
2. Check UI state
3. Attempt to send message
4. Verify error

**Expected Result**:
- UI indicates offline status
- Message sending is blocked or shows error
- Error clearly states offline condition
- No misleading errors

---

### Test 28: Test with Malformed API Response
**Objective**: Verify handling of bad API responses

**Steps**:
1. Configure tool to return malformed response
2. Send a message
3. Verify handling

**Expected Result**:
- Graceful error handling
- No crash
- Error indicates API issue
- User can retry

---

### Test 29: Verify Error Recovery
**Objective**: Verify recovery after error

**Steps**:
1. Trigger an error
2. Fix the issue (e.g., add valid API key)
3. Send another message
4. Verify normal operation

**Expected Result**:
- System recovers
- New messages work
- No state corruption
- Reconfiguration not needed

---

## Performance Tests

### Test 30: Test with Large Note Attachment
**Objective**: Verify performance with large attachments

**Steps**:
1. Create a large note (5000+ characters)
2. Ask AI: "Summarize this note"
3. Measure response time
4. Verify complete processing

**Expected Result**:
- Response received within reasonable time (< 30 seconds)
- All content is processed
- No timeouts
- Summary is complete

---

### Test 31: Test with Multiple Attachments
**Objective**: Verify performance with multiple contexts

**Steps**:
1. Attach 3-5 notes/tasks to context
2. Ask a question
3. Measure response time
4. Verify accuracy

**Expected Result**:
- Response time is acceptable
- All attachments are considered
- Response is accurate
- No memory issues

---

### Test 32: Measure Response Time for Simple Queries
**Objective**: Establish baseline performance

**Steps**:
1. Send simple query: "What is 2+2?"
2. Measure time from send to complete response
3. Record response time
4. Repeat 5 times
5. Calculate average

**Expected Result**:
- Response time < 5 seconds for simple queries
- Consistent timing
- No outliers
- Baseline established

---

### Test 33: Measure Response Time for Complex Tool Calls
**Objective**: Measure performance with tool usage

**Steps**:
1. Send: "Create a note, a task, and a calendar event"
2. Measure time to complete all operations
3. Record timing
4. Repeat 3 times
5. Calculate average

**Expected Result**:
- Response time < 15 seconds
- All operations complete
- No partial failures
- Consistent performance

---

## Test Execution Checklist

- [ ] All Configuration Flow Tests (5)
- [ ] All Basic Chat Tests (4)
- [ ] All Tool Usage Tests (9)
- [ ] All Edge Case Tests (5)
- [ ] All Error Scenario Tests (6)
- [ ] All Performance Tests (4)
- [ ] Total: 33 tests completed

## Bug Reporting Template

If a test fails, report with:

```markdown
### Test Failure: [Test Name]

**Test ID**: [Number]
**Category**: [Category Name]
**Steps Taken**:
1.
2.
3.

**Expected Result**:
[What should happen]

**Actual Result**:
[What actually happened]

**Device/Environment**:
- App version:
- GLM model:
- API key type:

**Logs**:
[Relevant log entries]

**Screenshots/Videos**:
[Attach if applicable]
```

## Notes

- Use a test GLM API key when possible (not production)
- Ensure stable network connection for most tests
- Document any deviations from expected behavior
- Record response times for performance tests
- Take screenshots of failures for reference
