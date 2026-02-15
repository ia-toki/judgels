import nock from 'nock';

import { APP_CONFIG } from '../../../../conf';
import { CourseErrors } from '../../../../modules/api/jerahmeel/course';
import { SubmissionError } from '../../../../modules/form/submissionError';

import * as courseActions from './courseActions';

const courseJid = 'course-jid';

describe('courseActions', () => {
  afterEach(function () {
    nock.cleanAll();
  });

  describe('createCourse()', () => {
    const params = { slug: 'new-course', name: 'New Course' };

    describe('when the slug does not already exist', () => {
      it('calls API to create course', async () => {
        nock(APP_CONFIG.apiUrl)
          .defaultReplyHeaders({ 'access-control-allow-origin': '*' })
          .options(`/courses`)
          .reply(200)
          .post(`/courses`, params)
          .reply(200);

        await courseActions.createCourse(params);
      });
    });

    describe('when the slug already exists', () => {
      it('throws SubmissionError', async () => {
        nock(APP_CONFIG.apiUrl)
          .defaultReplyHeaders({ 'access-control-allow-origin': '*' })
          .options(`/courses`)
          .reply(200)
          .post(`/courses`, params)
          .reply(400, { message: CourseErrors.SlugAlreadyExists });

        await expect(courseActions.createCourse(params)).rejects.toEqual(
          new SubmissionError({ slug: 'Slug already exists' })
        );
      });
    });
  });

  describe('updateCourse()', () => {
    const slug = 'old-slug';

    describe('when the slug is not updated', () => {
      const params = { name: 'New Name' };

      it('calls API to update course', async () => {
        nock(APP_CONFIG.apiUrl)
          .defaultReplyHeaders({ 'access-control-allow-origin': '*' })
          .options(`/courses/${courseJid}`)
          .reply(200)
          .post(`/courses/${courseJid}`, params)
          .reply(200);

        await courseActions.updateCourse(courseJid, params);
      });
    });

    describe('when the slug is updated', () => {
      const params = { slug: 'new-slug', name: 'New Name' };

      describe('when the slug does not already exist', () => {
        it('calls API to update course', async () => {
          nock(APP_CONFIG.apiUrl)
            .defaultReplyHeaders({ 'access-control-allow-origin': '*' })
            .options(`/courses/${courseJid}`)
            .reply(200)
            .post(`/courses/${courseJid}`, params)
            .reply(200);

          await courseActions.updateCourse(courseJid, params);
        });
      });

      describe('when the slug already exists', () => {
        it('throws SubmissionError', async () => {
          nock(APP_CONFIG.apiUrl)
            .defaultReplyHeaders({ 'access-control-allow-origin': '*' })
            .options(`/courses/${courseJid}`)
            .reply(200)
            .post(`/courses/${courseJid}`, params)
            .reply(400, { message: CourseErrors.SlugAlreadyExists });

          await expect(courseActions.updateCourse(courseJid, params)).rejects.toEqual(
            new SubmissionError({ slug: 'Slug already exists' })
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
      nock(APP_CONFIG.apiUrl)
        .defaultReplyHeaders({ 'access-control-allow-origin': '*' })
        .get(`/courses`)
        .reply(200, responseBody);

      const response = await courseActions.getCourses();
      expect(response).toEqual(responseBody);
    });
  });

  describe('getChapters()', () => {
    const responseBody = {
      data: [],
    };

    it('calls API to get chapters', async () => {
      nock(APP_CONFIG.apiUrl)
        .defaultReplyHeaders({ 'access-control-allow-origin': '*' })
        .get(`/courses/${courseJid}/chapters`)
        .reply(200, responseBody);

      const response = await courseActions.getChapters(courseJid);
      expect(response).toEqual(responseBody);
    });
  });

  describe('setChapters()', () => {
    const params = [];

    it('calls API to set chapters', async () => {
      nock(APP_CONFIG.apiUrl)
        .defaultReplyHeaders({ 'access-control-allow-origin': '*' })
        .options(`/courses/${courseJid}/chapters`)
        .reply(200)
        .put(`/courses/${courseJid}/chapters`, params)
        .reply(200);

      await courseActions.setChapters(courseJid, params);
    });
  });
});
