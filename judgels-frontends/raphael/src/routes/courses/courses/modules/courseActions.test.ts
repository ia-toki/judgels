import { push } from 'connected-react-router';
import nock from 'nock';
import { SubmissionError } from 'redux-form';
import configureMockStore from 'redux-mock-store';
import thunk from 'redux-thunk';

import { APP_CONFIG } from '../../../../conf';
import { CourseErrors, Course } from '../../../../modules/api/jerahmeel/course';
import * as courseActions from './courseActions';
import { PutCourse } from './courseReducer';

const courseJid = 'course-jid';
const course: Course = {
  id: 1,
  jid: courseJid,
  slug: 'competitive',
  name: 'Competitive',
};
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
    const params = { slug: 'new-course' };

    describe('when the slug does not already exist', () => {
      it('calls API to create course', async () => {
        nock(APP_CONFIG.apiUrls.jerahmeel)
          .defaultReplyHeaders({ 'access-control-allow-origin': '*' })
          .options(`/courses`)
          .reply(200)
          .post(`/courses`, params)
          .reply(200);

        await store.dispatch(courseActions.createCourse(params));

        expect(store.getActions()).toContainEqual(push('/courses/new-course'));
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

        await store.dispatch(courseActions.updateCourse(courseJid, slug, params));
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

          await store.dispatch(courseActions.updateCourse(courseJid, slug, params));

          expect(store.getActions()).toContainEqual(push('/courses/new-slug'));
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

          await expect(store.dispatch(courseActions.updateCourse(courseJid, slug, params))).rejects.toEqual(
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

  describe('getCourseBySlug()', () => {
    it('calls API to get course', async () => {
      nock(APP_CONFIG.apiUrls.jerahmeel)
        .defaultReplyHeaders({ 'access-control-allow-origin': '*' })
        .get(`/courses/slug/competitive`)
        .reply(200, course);

      const response = await store.dispatch(courseActions.getCourseBySlug('competitive'));
      expect(response).toEqual(course);

      expect(store.getActions()).toContainEqual(PutCourse.create(course));
    });
  });
});
