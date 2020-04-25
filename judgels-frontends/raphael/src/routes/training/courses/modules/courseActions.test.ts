import nock from 'nock';
import { SubmissionError } from 'redux-form';
import configureMockStore from 'redux-mock-store';
import thunk from 'redux-thunk';

import { APP_CONFIG } from '../../../../conf';
import { CourseErrors } from '../../../../modules/api/jerahmeel/course';
import * as courseActions from './courseActions';

const courseJid = 'course-jid';
const mockStore = configureMockStore([thunk]);

describe('courseActions', () => {
  let store;

  beforeEach(() => {
    store = mockStore({});
  });

  afterEach(function() {
    nock.cleanAll();
  });

  describe('createCourse()', () => {
    const params = { slug: 'new-course', name: 'New Course' };

    describe('when the slug does not already exist', () => {
      it('calls API to create course', async () => {
        nock(APP_CONFIG.apiUrls.jerahmeel)
          .defaultReplyHeaders({ 'access-control-allow-origin': '*' })
          .options(`/courses`)
          .reply(200)
          .post(`/courses`, params)
          .reply(200);

        await store.dispatch(courseActions.createCourse(params));
      });
    });

    describe('when the slug already exists', () => {
      it('throws SubmissionError', async () => {
        nock(APP_CONFIG.apiUrls.jerahmeel)
          .defaultReplyHeaders({ 'access-control-allow-origin': '*' })
          .options(`/courses`)
          .reply(200)
          .post(`/courses`, params)
          .reply(400, { errorName: CourseErrors.SlugAlreadyExists });

        await expect(store.dispatch(courseActions.createCourse(params))).rejects.toEqual(
          new SubmissionError({ slug: CourseErrors.SlugAlreadyExists })
        );
      });
    });
  });

  describe('updateCourse()', () => {
    const slug = 'old-slug';

    describe('when the slug is not updated', () => {
      const params = { name: 'New Name' };

      it('calls API to update course', async () => {
        nock(APP_CONFIG.apiUrls.jerahmeel)
          .defaultReplyHeaders({ 'access-control-allow-origin': '*' })
          .options(`/courses/${courseJid}`)
          .reply(200)
          .post(`/courses/${courseJid}`, params)
          .reply(200);

        await store.dispatch(courseActions.updateCourse(courseJid, params));
      });
    });

    describe('when the slug is updated', () => {
      const params = { slug: 'new-slug', name: 'New Name' };

      describe('when the slug does not already exist', () => {
        it('calls API to update course', async () => {
          nock(APP_CONFIG.apiUrls.jerahmeel)
            .defaultReplyHeaders({ 'access-control-allow-origin': '*' })
            .options(`/courses/${courseJid}`)
            .reply(200)
            .post(`/courses/${courseJid}`, params)
            .reply(200);

          await store.dispatch(courseActions.updateCourse(courseJid, params));
        });
      });

      describe('when the slug already exists', () => {
        it('throws SubmissionError', async () => {
          nock(APP_CONFIG.apiUrls.jerahmeel)
            .defaultReplyHeaders({ 'access-control-allow-origin': '*' })
            .options(`/courses/${courseJid}`)
            .reply(200)
            .post(`/courses/${courseJid}`, params)
            .reply(400, { errorName: CourseErrors.SlugAlreadyExists });

          await expect(store.dispatch(courseActions.updateCourse(courseJid, params))).rejects.toEqual(
            new SubmissionError({ slug: CourseErrors.SlugAlreadyExists })
          );
        });
      });
    });
  });

  describe('getCourses()', () => {
    const responseBody = {
      data: [],
    };

    it('calls API to get courses', async () => {
      nock(APP_CONFIG.apiUrls.jerahmeel)
        .defaultReplyHeaders({ 'access-control-allow-origin': '*' })
        .get(`/courses`)
        .reply(200, responseBody);

      const response = await store.dispatch(courseActions.getCourses());
      expect(response).toEqual(responseBody);
    });
  });

  describe('getChapters()', () => {
    const responseBody = {
      data: [],
    };

    it('calls API to get chapters', async () => {
      nock(APP_CONFIG.apiUrls.jerahmeel)
        .defaultReplyHeaders({ 'access-control-allow-origin': '*' })
        .get(`/courses/${courseJid}/chapters`)
        .reply(200, responseBody);

      const response = await store.dispatch(courseActions.getChapters(courseJid));
      expect(response).toEqual(responseBody);
    });
  });

  describe('setChapters()', () => {
    const params = [];

    it('calls API to set chapters', async () => {
      nock(APP_CONFIG.apiUrls.jerahmeel)
        .defaultReplyHeaders({ 'access-control-allow-origin': '*' })
        .options(`/courses/${courseJid}/chapters`)
        .reply(200)
        .put(`/courses/${courseJid}/chapters`, params)
        .reply(200);

      await store.dispatch(courseActions.setChapters(courseJid, params));
    });
  });
});
