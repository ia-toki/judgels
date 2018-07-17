import { createToastMiddleware } from './toastMiddleware';

describe('toastMiddleware', () => {
  let toastAction: jest.Mocked<any>;
  let store: jest.Mock<any>;

  const myAction = { type: 'action ' };
  const nextAction = { type: 'next_action ' };

  beforeEach(() => {
    toastAction = {
      showErrorToast: jest.fn(),
    };
    store = jest.fn();
  });

  describe('when the action throws error', () => {
    const error = new Error('error');
    const next = async action => {
      throw error;
    };
    const applyMiddleware = action => createToastMiddleware(toastAction)(store)(next)(action);

    it('shows the error toast', async () => {
      setTimeout(() => {
        expect(async () => {
          await applyMiddleware(myAction);
        }).toThrow('error');

        expect(toastAction.showErrorToast).toHaveBeenCalledWith(error);
      });
    });
  });

  describe('when the action does not throw any error', () => {
    const next = async action => nextAction;
    const applyMiddleware = action => createToastMiddleware(toastAction)(store)(next)(action);

    it('just passes the action through', async () => {
      expect(await applyMiddleware(myAction)).toEqual(nextAction);
      expect(toastAction.showErrorToast).not.toHaveBeenCalled();
    });
  });
});
