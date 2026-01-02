import { vi } from 'vitest';

import { NotFoundError } from '../api/error';
import toastMiddleware from './toastMiddleware';

import * as toastActions from './toastActions';

vi.mock('./toastActions');

describe('toastMiddleware', () => {
  let store;

  const myAction = { type: 'action ' };
  const nextAction = { type: 'next_action ' };

  beforeEach(() => {
    toastActions.showErrorToast.mockClear();
    store = vi.fn();
  });

  describe('when the action throws Error', () => {
    const error = new Error('error');
    const next = async action => {
      throw error;
    };
    const applyMiddleware = action => toastMiddleware(store)(next)(action);

    it('rethrows the error and shows the error toast', async () => {
      await expect(applyMiddleware(myAction)).rejects.toThrow(error);
      expect(toastActions.showErrorToast).toHaveBeenCalledWith(error);
    });
  });

  describe('when the action throws API error', () => {
    const error = new NotFoundError({ message: 'error' });
    const next = async action => {
      throw error;
    };
    const applyMiddleware = action => toastMiddleware(store)(next)(action);

    it('rethrows the error and shows the error toast', async () => {
      await expect(applyMiddleware(myAction)).rejects.toThrow(error);
      expect(toastActions.showErrorToast).toHaveBeenCalledWith(error);
    });
  });

  describe('when the action does not throw any error', () => {
    const next = async action => nextAction;
    const applyMiddleware = action => toastMiddleware(store)(next)(action);

    it('just passes the action through', async () => {
      expect(await applyMiddleware(myAction)).toEqual(nextAction);
      expect(toastActions.showErrorToast).not.toHaveBeenCalled();
    });
  });
});
