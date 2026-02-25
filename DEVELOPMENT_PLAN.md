# MyBrain Development Plan - Next Steps

**Date**: 2026-02-25
**Priority Order**: 1. Test Fixes → 2. Build Verification → 3. Tier 2 Features

---

## Phase 1: Achieve 100% Test Pass Rate

**Estimated Time**: 30-45 minutes
**Priority**: HIGH
**Status**: 🔄 READY TO START

### Task 1.1: Fix Date Format Tests (15 minutes)

**Affected Files**:
- `ai/data/src/test/java/com/mhss/app/data/tools/CalendarToolSetTest.kt`
- `ai/data/src/test/java/com/mhss/app/data/tools/TaskToolSetTest.kt`

**Changes Required**:

#### CalendarToolSetTest - Date Updates

```kotlin
// Line ~70-73: testGetEventsWithinRange
- startDateTime = "2024-01-15T09:00:00"
- endDateTime = "2024-01-15T15:00:00"
+ startDateTime = "09:00 15-01-2024"
+ endDateTime = "15:00 15-01-2024"

// Line ~104-108: testSearchEventsByNameWithinRange
// Apply same format change

// Line ~120-130: testCreateEvent
// Apply same format change

// Line ~142-152: testCreateEventWithRecurrence
// Apply same format change

// Line ~164-174: testCreateEvents
// Apply same format change
```

#### TaskToolSetTest - Date Updates

```kotlin
// Line ~68: testCreateTask
- dueDate = "2024-01-15T10:00:00"
+ dueDate = "10:00 15-01-2025"

// Line ~150, 156: testCreateMultipleTasksWithDueDates
// Apply same format change
```

**Verification**:
```bash
./gradlew :ai:data:test --tests CalendarToolSetTest
./gradlew :ai:data:test --tests TaskToolSetTest
```

**Expected Result**: All Calendar and Task date-related tests pass (7 tests fixed)

---

### Task 1.2: Fix Bookmark Mock Tests (10 minutes)

**Affected File**: `ai/data/src/test/java/com/mhss/app/data/tools/BookmarkToolSetTest.kt`

**Changes Required**:

```kotlin
// testCreateBookmark (~lines 32-44)
- val bookmarkId = 123L
- coEvery { addBookmark(any()) } returns bookmarkId
+ val bookmarkId = "550e8400-e29b-41d4-a716-446655440000"
+ val bookmarkSlot = slot<Bookmark>()
+ coEvery { addBookmark(capture(bookmarkSlot)) } answers {
+     bookmarkSlot.captured.id
+ }

- assertEquals("Should return the bookmark ID", bookmarkId, result.createdBookmarkId)
+ assertEquals("Should return a bookmark ID", 36, result.createdBookmarkId.length)
+ assertTrue("Should return a valid UUID", result.createdBookmarkId.matches(Regex("[0-9a-fA-F-]{36}")))

// Apply similar changes to:
// - testCreateBookmarkMinimal (~lines 47-61)
// - testCreateBookmarkWithTitleOnly (~lines 64-77)
```

**Verification**:
```bash
./gradlew :ai:data:test --tests BookmarkToolSetTest
```

**Expected Result**: All Bookmark tests pass (3 tests fixed)

---

### Task 1.3: Fix MockK Configuration Issues (10 minutes)

**Affected Files**:
- `ai/data/src/test/java/com/mhss/app/data/tools/TaskToolSetTest.kt`
- `ai/data/src/test/java/com/mhss/app/data/tools/CalendarToolSetTest.kt`

#### TaskToolSetTest - testSearchTasks (~line 50)

```kotlin
- coEvery { searchTasksByName("test").first() } returns expectedTasks
+ coEvery { searchTasksByName("test") } returns flowOf(expectedTasks)
```

#### TaskToolSetTest - testUpdateTaskCompleted (~lines 105-118)

```kotlin
// Add before taskToolSet.updateTaskCompleted call:
+ coEvery { updateTaskCompletedUseCase(any(), any()) } just Runs
```

#### CalendarToolSetTest - testGetAllCalendars (~lines 198-200)

```kotlin
- coEvery {
-     getPreference(stringSetPreferencesKey(PrefsConstants.EXCLUDED_CALENDARS_KEY), emptySet())
- } returns flowOf(emptySet<String>())
+ every { getPreference(any(), any()) } returns flowOf(emptySet<String>())
```

**Verification**:
```bash
./gradlew :ai:data:test --tests TaskToolSetTest
./gradlew :ai:data:test --tests CalendarToolSetTest
```

**Expected Result**: MockK configuration tests pass (3 tests fixed)

---

### Task 1.4: Review testToolCallLimitExceeded (10-20 minutes)

**Affected File**: `ai/data/src/test/java/com/mhss/app/data/repository/AiRepositoryImplTest.kt`

**Action**: Review implementation to understand actual tool call limit behavior, then either:
- Fix test to properly trigger the limit, OR
- Update test expectations to match actual behavior

**Verification**:
```bash
./gradlew :ai:data:test --tests AiRepositoryImplTest
```

**Expected Result**: Test either passes or is updated with proper expectations

---

### Task 1.5: Full Test Suite Verification (5 minutes)

```bash
# Run all tests
./gradlew :ai:data:test --rerun-tasks

# Expected: 52/52 tests passing (100%)
```

---

## Phase 2: Build Verification & Release

**Estimated Time**: 15-20 minutes
**Priority**: HIGH
**Status**: ⏳ BLOCKED until Phase 1 complete

### Task 2.1: Commit Test Fixes

```bash
git add .
git commit -m "Fix all test failures: date formats, mocks, and test logic

- Update CalendarToolSetTest to use HH:mm dd-MM-yyyy format
- Update TaskToolSetTest date formats
- Fix BookmarkToolSetTest UUID mock returns
- Fix MockK flow and verification setup
- Review and update toolCallLimitExceeded test

Test Pass Rate: 100% (52/52 tests passing)"
```

### Task 2.2: Push to GitHub

```bash
git push origin master
```

### Task 2.3: Monitor GitHub Actions Build

1. Go to: https://github.com/N-cryptd/MyBrain/actions
2. Watch for workflow run
3. Verify build completes successfully
4. Check artifacts uploaded

### Task 2.4: Download and Test APK

1. Download `debug-apk` artifact
2. Install on Android device
3. Verify app launches
4. Test GLM AI configuration
5. Verify core features work

### Task 2.5: Create Release (Optional)

If everything works:
```bash
gh release create v3.0.2 \
  --title "MyBrain v3.0.2 - Test Suite Fixed" \
  --notes "All tests passing (52/52), verified build quality"
```

---

## Phase 3: Branch Synchronization

**Estimated Time**: 10 minutes
**Priority**: MEDIUM
**Status**: ⏳ BLOCKED until Phase 2 complete

### Option A: Merge origin/dev into master

```bash
git fetch origin
git checkout master
git merge origin/dev
git push origin master
```

### Option B: Rebase origin/dev onto master

```bash
git fetch origin
git checkout dev
git rebase master
git push origin dev --force-with-lease
```

**Recommendation**: Use Option A if master is production-ready, Option B if dev should be the source of truth.

---

## Phase 4: Begin Tier 2 Development

**Estimated Time**: 2-3 days
**Priority**: MEDIUM
**Status**: ⏳ BLOCKED until Phase 3 complete

### Task 4.1: Smart Note Linking (First Tier 2 Feature)

**Architecture Design**:

1. **Data Model Changes**:
   ```kotlin
   // NoteEntity.kt
   @Entity(tableName = "notes")
   data class NoteEntity(
       @PrimaryKey val id: Long = 0,
       val title: String,
       val content: String,
       val createdDate: Long,
       val modifiedDate: Long,
       val folderId: Long? = null,
       val isPinned: Boolean = false,
       val isFavorite: Boolean = false,
       + val linkedNoteIds: String = ""  // JSON array of note IDs
       + val backlinkCount: Int = 0       // Count of notes linking to this
   )
   ```

2. **Linking Engine**:
   ```kotlin
   // NoteLinkingEngine.kt
   class NoteLinkingEngine {
       fun detectLinks(content: String): Set<Long>
       fun createLink(fromNoteId: Long, toNoteId: Long)
       fun removeLink(fromNoteId: Long, toNoteId: Long)
       fun getLinkedNotes(noteId: Long): List<Note>
       fun getBacklinks(noteId: Long): List<Note>
       fun searchByLink(query: String): List<Note>
   }
   ```

3. **Link Syntax**:
   - `[[Note Title]]` - Link to note by title
   - `[[123]]` - Link to note by ID
   - Auto-complete in editor
   - Show backlinks on note detail

4. **UI Components**:
   - Note link detector (highlight [[links]])
   - Link insertion dialog
   - Backlinks panel
   - Linked notes visualization

**Implementation Steps**:
1. Update NoteEntity schema (migration)
2. Create NoteLinkingEngine
3. Implement link detection in content
4. Create note link UI component
5. Add backlinks panel to NoteDetailScreen
6. Add link insertion to NoteEditor
7. Update Note AI tool to support linking

**Estimated Time**: 1 day

---

### Task 4.2: AI-Powered Universal Search

**Architecture Design**:

1. **Search Index**:
   ```kotlin
   // SearchIndex.kt
   data class SearchableItem(
       val id: String,
       val type: SearchableType,  // NOTE, TASK, EVENT, DIARY, BOOKMARK
       val title: String,
       val content: String,
       val metadata: Map<String, Any>,
       val relevanceScore: Float
   )

   enum class SearchableType {
       NOTE, TASK, CALENDAR_EVENT, DIARY_ENTRY, BOOKMARK
   }
   ```

2. **Search Engine**:
   ```kotlin
   // UniversalSearchEngine.kt
   class UniversalSearchEngine(
       private val noteRepo: NoteRepository,
       private val taskRepo: TaskRepository,
       private val calendarRepo: CalendarRepository,
       private val diaryRepo: DiaryRepository,
       private val bookmarkRepo: BookmarkRepository
   ) {
       suspend fun search(query: String): List<SearchableItem>
       suspend fun searchWithAI(query: String): List<SearchableItem>
   }
   ```

3. **AI Enhancement**:
   - Use GLM to understand semantic intent
   - Re-rank results based on AI analysis
   - Support natural language queries
   - "Show me tasks due this week"
   - "Notes about meetings"

4. **UI Components**:
   - Search bar with AI toggle
   - Results list with type badges
   - Filter by type
   - Result preview

**Implementation Steps**:
1. Create SearchIndex data model
2. Implement UniversalSearchEngine
3. Create unified search repository
4. Add AI semantic search integration
5. Create search UI components
6. Add search to dashboard
7. Create search settings

**Estimated Time**: 1 day

---

## Phase 5: Manual Testing

**Estimated Time**: 2-3 hours
**Priority**: MEDIUM
**Status**: ⏳ READY TO START (can run in parallel)

Follow test plan in `GLM_TESTING_MANUAL.md`:

### Test Categories:
1. Configuration Flow Tests (5)
2. Basic Chat Tests (4)
3. Tool Usage Tests (9)
4. Edge Case Tests (5)
5. Error Scenario Tests (6)
6. Performance Tests (4)

**Total**: 33 test scenarios

---

## Success Metrics

### Phase 1 Success Criteria:
- [ ] 52/52 tests passing (100%)
- [ ] Test execution time < 2 minutes
- [ ] No flaky tests

### Phase 2 Success Criteria:
- [ ] GitHub Actions build succeeds
- [ ] APK generated and downloadable
- [ ] APK installs on device
- [ ] App launches and core features work

### Phase 3 Success Criteria:
- [ ] Branches synchronized
- [ ] Git history clean
- [ ] No merge conflicts

### Phase 4 Success Criteria (First Feature):
- [ ] Smart Note Linking implemented
- [ ] Tests pass for new features
- [ ] UI implemented and tested
- [ ] Documentation updated

---

## Contingency Plans

### If Tests Don't Pass After Fixes:
1. Review actual implementation vs test expectations
2. Check if test data is correct
3. Verify mock setups match actual use case behavior
4. Consider if implementation needs adjustment

### If GitHub Actions Build Fails:
1. Check workflow logs for errors
2. Verify Android SDK versions match
3. Check dependency compatibility
4. Review recent commits for issues

### If APK Has Runtime Issues:
1. Check logcat for crash logs
2. Verify GLM API key configuration
3. Test on multiple Android versions
4. Review ProGuard/R8 rules if release build

---

## Timeline Summary

| Phase | Tasks | Duration | Dependencies |
|-------|-------|----------|--------------|
| Phase 1 | Fix test suite | 30-45 min | None |
| Phase 2 | Build & verify | 15-20 min | Phase 1 |
| Phase 3 | Sync branches | 10 min | Phase 2 |
| Phase 4 | Tier 2 features | 2-3 days | Phase 3 |
| Phase 5 | Manual testing | 2-3 hours | Parallel |

**Total Time to Production-Ready APK**: ~1 hour (Phases 1-3)
**Total Time to First Tier 2 Feature**: ~2-3 days

---

**Next Action**: Start Phase 1, Task 1.1 - Fix Date Format Tests
**Ready to Proceed**: ✅ YES
