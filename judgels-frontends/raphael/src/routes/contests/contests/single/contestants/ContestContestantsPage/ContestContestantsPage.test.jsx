import { mount } from 'enzyme';
import * as React from 'react';
import { IntlProvider } from 'react-intl';
import { Provider } from 'react-redux';
import { MemoryRouter } from 'react-router';
import { applyMiddleware, combineReducers, createStore } from 'redux';
import { reducer as formReducer } from 'redux-form';
import thunk from 'redux-thunk';

import { contest } from '../../../../../../fixtures/state';
import ContestContestantsPage from './ContestContestantsPage';
import contestReducer, { PutContest } from '../../../modules/contestReducer';
import * as contestContestantActions from '../../modules/contestContestantActions';
import * as contestActions from '../../../modules/contestActions';

jest.mock('../../modules/contestContestantActions');
jest.mock('../../../modules/contestActions');

describe('ContestContestantsPage', () => {
  let wrapper;

  const response = {
    data: { page: [], totalCount: 0 },
    profilesMap: {
      userJid1: { username: 'user1' },
      userJid2: { username: 'user2' },
    },
    config: {
      canManage: true,
    },
  };

  const render = () => {
    const store = createStore(
      combineReducers({ uriel: combineReducers({ contest: contestReducer }), form: formReducer }),
      applyMiddleware(thunk)
    );
    store.dispatch(PutContest(contest));

    wrapper = mount(
      <IntlProvider locale={navigator.language}>
        <Provider store={store}>
          <MemoryRouter>
            <ContestContestantsPage />
          </MemoryRouter>
        </Provider>
      </IntlProvider>
    );
  };

  beforeEach(() => {
    contestContestantActions.getContestants.mockReturnValue(() => Promise.resolve(response));
    contestActions.resetVirtualContest.mockReturnValue(() => Promise.resolve({}));
  });

  describe('when there are no contestants', () => {
    beforeEach(() => {
      render();
    });

    it('shows placeholder text and no contestants', async () => {
      await new Promise(resolve => setImmediate(resolve));
      wrapper.update();

      expect(wrapper.text()).toContain('No contestants.');
      expect(wrapper.find('tr')).toHaveLength(0);
    });
  });

  describe('when there are contestants', () => {
    beforeEach(() => {
      const contestants = [
        {
          userJid: 'userJid1',
        },
        {
          userJid: 'userJid2',
        },
      ];
      contestContestantActions.getContestants.mockReturnValue(() =>
        Promise.resolve({ ...response, data: { page: contestants, totalCount: 2 } })
      );

      render();
    });

    it('shows the contestants', async () => {
      await new Promise(resolve => setImmediate(resolve));
      wrapper.update();

      expect(wrapper.find('tr')).toHaveLength(3);
    });
  });
});
