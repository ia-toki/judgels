import { UnauthorizedError } from 'modules/api/error';

import { createTokenGateMiddleware } from './tokenGateMiddleware';

describe('tokenGateMiddleware', () => {
  let tokenGateActions: jest.Mocked<any>;
  let store: jest.Mock<any>;

  const myAction = { type: 'action' };
  const nextAction = { type: 'next_action' };

  beforeEach(() => {
    tokenGateActions = {
      redirectToLogout: jest.fn(),
    };
    store = jest.fn();
  });

  describe('when the action throws UnauthorizedError', () => {
    const error = new UnauthorizedError('token expired' as any);
    const next = async action => {
      throw error;
    };
    const applyMiddleware = action => createTokenGateMiddleware(tokenGateActions)(store)(next)(action);

    it('redirects to logout', async () => {
      setTimeout(() => {
        expect(async () => {
          await applyMiddleware(myAction);
        }).toThrow('token expired');

        expect(tokenGateActions.redirectToLogout).toHaveBeenCalled();
      });
    });
  });

  describe('when the action does not throw any error', () => {
    const next = async action => nextAction;
    const applyMiddleware = action => createTokenGateMiddleware(tokenGateActions)(store)(next)(action);

    it('just passes the action through', async () => {
      expect(await applyMiddleware(myAction)).toEqual(nextAction);
      expect(tokenGateActions.redirectToLogout).not.toHaveBeenCalled();
    });
  });
});
