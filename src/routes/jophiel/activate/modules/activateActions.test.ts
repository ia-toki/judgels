import { activateActions } from './activateActions';

describe('activateActions', () => {
  let dispatch: jest.Mock<any>;
  let getState: jest.Mock<any>;

  let userAccountAPI: jest.Mocked<any>;

  beforeEach(() => {
    dispatch = jest.fn();
    getState = jest.fn();

    userAccountAPI = {
      activateUser: jest.fn(),
    };
  });

  describe('activate()', () => {
    const { activate } = activateActions;
    const emailCode = 'code';
    const doActivate = async () => activate(emailCode)(dispatch, getState, { userAccountAPI });

    beforeEach(async () => {
      await doActivate();
    });

    it('calls API to activate user', async () => {
      expect(userAccountAPI.activateUser).toHaveBeenCalledWith(emailCode);
    });
  });
});
