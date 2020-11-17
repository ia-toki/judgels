import { mount, ReactWrapper } from 'enzyme';
import * as React from 'react';
import { IntlProvider } from 'react-intl';
import { Provider } from 'react-redux';
import { MemoryRouter } from 'react-router';
import { applyMiddleware, combineReducers, createStore } from 'redux';
import { reducer as formReducer } from 'redux-form';
import thunk from 'redux-thunk';

import { contest } from '../../../../../../fixtures/state';
import { ContestContestant, ContestContestantsResponse } from '../../../../../../modules/api/uriel/contestContestant';
import ContestContestantsPage from './ContestContestantsPage';
import { contestReducer, PutContest } from '../../../modules/contestReducer';
import * as contestContestantActions from '../../modules/contestContestantActions';
import * as contestActions from '../../../modules/contestActions';

jest.mock('../../modules/contestContestantActions');
jest.mock('../../../modules/contestActions');

describe('ContestContestantsPage', () => {
  let wrapper: ReactWrapper<any, any>;

  const response: ContestContestantsResponse = {
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
    const store: any = createStore(
      combineReducers({ uriel: combineReducers({ contest: contestReducer }), form: formReducer }),
      applyMiddleware(thunk)
    );
    store.dispatch(PutContest.create(contest));

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
    (contestContestantActions.getContestants as jest.Mock).mockReturnValue(() => Promise.resolve(response));
    (contestActions.resetVirtualContest as jest.Mock).mockReturnValue(() => Promise.resolve({}));
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
      const contestants: ContestContestant[] = [
        {
          userJid: 'userJid1',
        } as ContestContestant,
        {
          userJid: 'userJid2',
        } as ContestContestant,
      ];
      (contestContestantActions.getContestants as jest.Mock).mockReturnValue(() =>
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
