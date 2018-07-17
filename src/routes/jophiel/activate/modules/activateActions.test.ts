import { activateActions } from './activateActions';

describe('activateActions', () => {
  let dispatch: jest.Mock<any>;
  let getState: jest.Mock<any>;

  let userAPI: jest.Mocked<any>;

  beforeEach(() => {
    dispatch = jest.fn();
    getState = jest.fn();

    userAPI = {
      activateUser: jest.fn(),
    };
  });

  describe('activate()', () => {
    const { activate } = activateActions;
    const emailCode = 'code';
    const doActivate = async () => activate(emailCode)(dispatch, getState, { userAPI });

    beforeEach(async () => {
      await doActivate();
    });

    it('calls API to activate user', async () => {
      expect(userAPI.activateUser).toHaveBeenCalledWith(emailCode);
    });
  });
});
