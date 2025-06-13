package com.basejava.webapp.storage;

import com.basejava.webapp.exception.ExistStorageException;
import com.basejava.webapp.exception.NotExistStorageException;
import com.basejava.webapp.exception.StorageException;
import com.basejava.webapp.model.Resume;
import org.junit.jupiter.api.BeforeEach;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

public abstract class AbstractArrayStorageTest {

    private final Storage storage;

    private static final String UUID_1 = "uuid1";
    protected static final Resume RESUME_1 = new Resume(UUID_1);
    private static final String UUID_2 = "uuid2";
    protected static final Resume RESUME_2 = new Resume(UUID_2);
    private static final String UUID_3 = "uuid3";
    protected static final Resume RESUME_3 = new Resume(UUID_3);
    private static final String UUID_4 = "uuid4";
    protected static final Resume RESUME_4 = new Resume(UUID_4);

    protected AbstractArrayStorageTest(Storage storage) {
        this.storage = storage;
    }

    @BeforeEach
    public void setUp() {
        storage.clear();
        storage.save(RESUME_1);
        assertGet(RESUME_1);
        storage.save(RESUME_2);
        assertGet(RESUME_2);
        storage.save(RESUME_3);
        assertGet(RESUME_3);
    }

    @org.junit.jupiter.api.Test
    void getAll() {
        Resume[] expected = {RESUME_1, RESUME_2, RESUME_3};
        assertGetAll(expected);
    }

    @org.junit.jupiter.api.Test
    void get() {
        assertGet(RESUME_1);
        assertGet(RESUME_2);
        assertGet(RESUME_3);
    }

    @org.junit.jupiter.api.Test
    void getNotExist() {
        assertThrows(NotExistStorageException.class, () -> assertGet(RESUME_4));
    }

    @org.junit.jupiter.api.Test
    void size() {
        assertSize(3);
    }

    @org.junit.jupiter.api.Test
    void save() {
        storage.save(RESUME_4);
        assertGet(RESUME_4);
        assertSize(4);
    }

    @org.junit.jupiter.api.Test
    void saveExist() {
        assertThrows(ExistStorageException.class, () -> storage.save(RESUME_1));
    }

    @org.junit.jupiter.api.Test
    void saveStorageOverflow() {
        storage.clear();
        try {
            for (int i = 0; i < AbstractArrayStorage.STORAGE_LIMIT; i++) {
                storage.save(new Resume());
            }
        } catch (StorageException e) {
            fail("Переполнение произошло раньше времени!");
        }
        assertThrows(StorageException.class, () -> storage.save(new Resume()));
    }

    @org.junit.jupiter.api.Test
    void delete() {
        storage.delete(RESUME_3.getUuid());
        assertSize(2);
        assertThrows(NotExistStorageException.class, () -> assertGet(RESUME_3));
    }

    @org.junit.jupiter.api.Test
    void deleteNotExist() {
        assertThrows(NotExistStorageException.class, () -> storage.delete(RESUME_4.getUuid()));
    }

    @org.junit.jupiter.api.Test
    void update() {
        storage.update(RESUME_3);
        assertSame(RESUME_3, storage.get(RESUME_3.getUuid()));
    }

    @org.junit.jupiter.api.Test
    void updateNotExist() {
        assertThrows(NotExistStorageException.class, () -> storage.update(RESUME_4));
    }

    @org.junit.jupiter.api.Test
    void clear() {
        storage.clear();
        assertSize(0);
        Resume[] resumes = {};
        assertGetAll(resumes);
    }

    private void assertGetAll(Resume[] resumes) {
        assertArrayEquals(resumes, storage.getAll());
    }

    private void assertGet(Resume resume) {
        assertEquals(resume, storage.get(resume.getUuid()));
    }

    private void assertSize(int size) {
        assertEquals(size, storage.size());
    }
}