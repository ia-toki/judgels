import nock from 'nock';
import configureMockStore from 'redux-mock-store';
import thunk from 'redux-thunk';

import { SubmissionError } from '../../../../modules/form/submissionError';
import { nockJophiel } from '../../../../utils/nock';

import * as registerActions from './registerActions';

const mockStore = configureMockStore([thunk]);

describe('registerActions', () => {
  let store;

  beforeEach(() => {
    store = mockStore({});
  });

  afterEach(function () {
    nock.cleanAll();
  });

  describe('registerUser()', () => {
    const data = {
      username: 'user',
      name: 'name',
      email: 'email@domain.com',
      password: 'pass',
    };

    describe('when username already exists', () => {
      it('throws SubmissionError', async () => {
        nockJophiel().get(`/user-search/username-exists/${data.username}`).reply(200, 'true');

        nockJophiel().get(`/user-search/email-exists/${data.email}`).reply(200, 'false');

        await expect(store.dispatch(registerActions.registerUser(data))).rejects.toEqual(
          new SubmissionError({ username: 'Username already exists' })
        );
      });
    });

    describe('when email already exists', () => {
      it('throws SubmissionError', async () => {
        nockJophiel().get(`/user-search/username-exists/${data.username}`).reply(200, 'false');
        nockJophiel().get(`/user-search/email-exists/${data.email}`).reply(200, 'true');

        await expect(store.dispatch(registerActions.registerUser(data))).rejects.toEqual(
          new SubmissionError({ email: 'Email already exists' })
        );
      });
    });

    describe('when the form is valid', () => {
      it('tries to register user', async () => {
        nockJophiel().get(`/user-search/username-exists/${data.username}`).reply(200, 'false');

        nockJophiel().get(`/user-search/email-exists/${data.email}`).reply(200, 'false');

        nockJophiel().post(`/user-account/register`, data).reply(200);

        await store.dispatch(registerActions.registerUser(data));
      });
    });
  });
});
