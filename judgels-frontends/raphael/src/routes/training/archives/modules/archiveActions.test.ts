import nock from 'nock';
import { SubmissionError } from 'redux-form';
import configureMockStore from 'redux-mock-store';
import thunk from 'redux-thunk';

import { APP_CONFIG } from '../../../../conf';
import { ArchiveErrors } from '../../../../modules/api/jerahmeel/archive';
import * as archiveActions from './archiveActions';

const archiveJid = 'archive-jid';
const mockStore = configureMockStore([thunk]);

describe('archiveActions', () => {
  let store;

  beforeEach(() => {
    store = mockStore({});
  });

  afterEach(function() {
    nock.cleanAll();
  });

  describe('createArchive()', () => {
    const params = { slug: 'new-archive', name: 'New Archive', category: 'Category' };

    describe('when the slug does not already exist', () => {
      it('calls API to create archive', async () => {
        nock(APP_CONFIG.apiUrls.jerahmeel)
          .defaultReplyHeaders({ 'access-control-allow-origin': '*' })
          .options(`/archives`)
          .reply(200)
          .post(`/archives`, params)
          .reply(200);

        await store.dispatch(archiveActions.createArchive(params));
      });
    });

    describe('when the slug already exists', () => {
      it('throws SubmissionError', async () => {
        nock(APP_CONFIG.apiUrls.jerahmeel)
          .defaultReplyHeaders({ 'access-control-allow-origin': '*' })
          .options(`/archives`)
          .reply(200)
          .post(`/archives`, params)
          .reply(400, { errorName: ArchiveErrors.SlugAlreadyExists });

        await expect(store.dispatch(archiveActions.createArchive(params))).rejects.toEqual(
          new SubmissionError({ slug: ArchiveErrors.SlugAlreadyExists })
        );
      });
    });
  });

  describe('updateArchive()', () => {
    describe('when the slug is not updated', () => {
      const params = { name: 'New Name' };

      it('calls API to update archive', async () => {
        nock(APP_CONFIG.apiUrls.jerahmeel)
          .defaultReplyHeaders({ 'access-control-allow-origin': '*' })
          .options(`/archives/${archiveJid}`)
          .reply(200)
          .post(`/archives/${archiveJid}`, params)
          .reply(200);

        await store.dispatch(archiveActions.updateArchive(archiveJid, params));
      });
    });

    describe('when the slug is updated', () => {
      const params = { slug: 'new-slug', name: 'New Name' };

      describe('when the slug does not already exist', () => {
        it('calls API to update archive', async () => {
          nock(APP_CONFIG.apiUrls.jerahmeel)
            .defaultReplyHeaders({ 'access-control-allow-origin': '*' })
            .options(`/archives/${archiveJid}`)
            .reply(200)
            .post(`/archives/${archiveJid}`, params)
            .reply(200);

          await store.dispatch(archiveActions.updateArchive(archiveJid, params));
        });
      });

      describe('when the slug already exists', () => {
        it('throws SubmissionError', async () => {
          nock(APP_CONFIG.apiUrls.jerahmeel)
            .defaultReplyHeaders({ 'access-control-allow-origin': '*' })
            .options(`/archives/${archiveJid}`)
            .reply(200)
            .post(`/archives/${archiveJid}`, params)
            .reply(400, { errorName: ArchiveErrors.SlugAlreadyExists });

          await expect(store.dispatch(archiveActions.updateArchive(archiveJid, params))).rejects.toEqual(
            new SubmissionError({ slug: ArchiveErrors.SlugAlreadyExists })
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
      nock(APP_CONFIG.apiUrls.jerahmeel)
        .defaultReplyHeaders({ 'access-control-allow-origin': '*' })
        .get(`/archives`)
        .reply(200, responseBody);

      const response = await store.dispatch(archiveActions.getArchives());
      expect(response).toEqual(responseBody);
    });
  });
});
