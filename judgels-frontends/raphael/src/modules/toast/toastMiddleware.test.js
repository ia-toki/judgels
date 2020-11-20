import { SubmissionError } from 'redux-form';

import { NotFoundError } from '../api/error';
import toastMiddleware from './toastMiddleware';
import * as toastActions from './toastActions';

jest.mock('./toastActions');

describe('toastMiddleware', () => {
  let store;

  const myAction = { type: 'action ' };
  const nextAction = { type: 'next_action ' };

  beforeEach(() => {
    toastActions.showErrorToast.mockClear();
    store = jest.fn();
  });

  describe('when the action throws Error', () => {
    const error = new Error('error');
    const next = async action => {
      throw error;
    };
    const applyMiddleware = action => toastMiddleware(store)(next)(action);

    it('shows the error toast', async () => {
      await applyMiddleware(myAction);
      expect(toastActions.showErrorToast).toHaveBeenCalledWith(error);
    });
  });

  describe('when the action throws SubmissionError', () => {
    const error = new SubmissionError({ field: 'error' });
    const next = async action => {
      throw error;
    };
    const applyMiddleware = action => toastMiddleware(store)(next)(action);

    it('rethrows the error and shows the error toast', async () => {
      await expect(applyMiddleware(myAction)).rejects.toMatchObject(error);
      expect(toastActions.showErrorToast).toHaveBeenCalledWith(error);
    });
  });

  describe('when the action throws other error', () => {
    const error = new NotFoundError({ errorName: 'error' });
    const next = async action => {
      throw error;
    };
    const applyMiddleware = action => toastMiddleware(store)(next)(action);

    it('rethrows the error and shows the error toast', async () => {
      await expect(applyMiddleware(myAction)).rejects.toMatchObject(error);
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
