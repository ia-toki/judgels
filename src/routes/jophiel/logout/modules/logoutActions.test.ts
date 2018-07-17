import { logoutActions } from './logoutActions';
import { UnauthorizedError } from '../../../../modules/api/error';
import { DelSession } from '../../../../modules/session/sessionReducer';
import { AppState } from '../../../../modules/store';
import { sessionState, token } from '../../../../fixtures/state';

describe('logoutActions', () => {
  let dispatch: jest.Mock<any>;
  const getState = (): Partial<AppState> => ({ session: sessionState });

  let sessionAPI: jest.Mocked<any>;
  let legacySessionAPI: jest.Mocked<any>;

  beforeEach(() => {
    dispatch = jest.fn();

    sessionAPI = {
      logOut: jest.fn(),
    };
    legacySessionAPI = {
      postLogout: jest.fn(),
    };
  });

  describe('logOut()', () => {
    const { logOut } = logoutActions;
    const doLogOut = async () => logOut('path')(dispatch, getState, { sessionAPI, legacySessionAPI });

    it('calls API to log out', async () => {
      await doLogOut();

      expect(sessionAPI.logOut).toHaveBeenCalledWith(token);
    });

    describe('when the logout is successful', () => {
      beforeEach(async () => {
        await doLogOut();
      });

      it('deletes the session', () => {
        expect(dispatch).toHaveBeenCalledWith(DelSession.create());
      });

      it('redirects to post logout url', () => {
        expect(legacySessionAPI.postLogout).toHaveBeenCalledWith('path');
      });
    });

    describe('when the current token is already invalid', () => {
      beforeEach(async () => {
        sessionAPI.logOut.mockImplementation(() => {
          throw new UnauthorizedError();
        });

        await doLogOut();
      });

      it('ends to session anyway', () => {
        expect(dispatch).toHaveBeenCalledWith(DelSession.create());
      });
    });
  });
});
