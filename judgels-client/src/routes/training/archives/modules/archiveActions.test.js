import nock from 'nock';

import { APP_CONFIG } from '../../../../conf';
import { ArchiveErrors } from '../../../../modules/api/jerahmeel/archive';
import { SubmissionError } from '../../../../modules/form/submissionError';

import * as archiveActions from './archiveActions';

const archiveJid = 'archive-jid';

describe('archiveActions', () => {
  afterEach(function () {
    nock.cleanAll();
  });

  describe('createArchive()', () => {
    const params = { slug: 'new-archive', name: 'New Archive', category: 'Category' };

    describe('when the slug does not already exist', () => {
      it('calls API to create archive', async () => {
        nock(APP_CONFIG.apiUrl)
          .defaultReplyHeaders({ 'access-control-allow-origin': '*' })
          .options(`/archives`)
          .reply(200)
          .post(`/archives`, params)
          .reply(200);

        await archiveActions.createArchive(params);
      });
    });

    describe('when the slug already exists', () => {
      it('throws SubmissionError', async () => {
        nock(APP_CONFIG.apiUrl)
          .defaultReplyHeaders({ 'access-control-allow-origin': '*' })
          .options(`/archives`)
          .reply(200)
          .post(`/archives`, params)
          .reply(400, { message: ArchiveErrors.SlugAlreadyExists });

        await expect(archiveActions.createArchive(params)).rejects.toEqual(
          new SubmissionError({ slug: 'Slug already exists' })
        );
      });
    });
  });

  describe('updateArchive()', () => {
    describe('when the slug is not updated', () => {
      const params = { name: 'New Name' };

      it('calls API to update archive', async () => {
        nock(APP_CONFIG.apiUrl)
          .defaultReplyHeaders({ 'access-control-allow-origin': '*' })
          .options(`/archives/${archiveJid}`)
          .reply(200)
          .post(`/archives/${archiveJid}`, params)
          .reply(200);

        await archiveActions.updateArchive(archiveJid, params);
      });
    });

    describe('when the slug is updated', () => {
      const params = { slug: 'new-slug', name: 'New Name' };

      describe('when the slug does not already exist', () => {
        it('calls API to update archive', async () => {
          nock(APP_CONFIG.apiUrl)
            .defaultReplyHeaders({ 'access-control-allow-origin': '*' })
            .options(`/archives/${archiveJid}`)
            .reply(200)
            .post(`/archives/${archiveJid}`, params)
            .reply(200);

          await archiveActions.updateArchive(archiveJid, params);
        });
      });

      describe('when the slug already exists', () => {
        it('throws SubmissionError', async () => {
          nock(APP_CONFIG.apiUrl)
            .defaultReplyHeaders({ 'access-control-allow-origin': '*' })
            .options(`/archives/${archiveJid}`)
            .reply(200)
            .post(`/archives/${archiveJid}`, params)
            .reply(400, { message: ArchiveErrors.SlugAlreadyExists });

          await expect(archiveActions.updateArchive(archiveJid, params)).rejects.toEqual(
            new SubmissionError({ slug: 'Slug already exists' })
          );
        });
      });
    });
  });

  describe('getArchives()', () => {
    const responseBody = {
      data: [],
    };

    it('calls API to get archives', async () => {
      nock(APP_CONFIG.apiUrl)
        .defaultReplyHeaders({ 'access-control-allow-origin': '*' })
        .get(`/archives`)
        .reply(200, responseBody);

      const response = await archiveActions.getArchives();
      expect(response).toEqual(responseBody);
    });
  });
});
