import { mount, ReactWrapper } from 'enzyme';
import * as React from 'react';
import { IntlProvider } from 'react-intl';
import { Provider } from 'react-redux';
import { MemoryRouter } from 'react-router';
import { applyMiddleware, combineReducers, createStore } from 'redux';
import { reducer as formReducer } from 'redux-form';
import thunk from 'redux-thunk';

import { contest } from 'fixtures/state';
import { ContestSupervisor, ContestSupervisorsResponse } from 'modules/api/uriel/contestSupervisor';

import { createContestSupervisorsPage } from './ContestSupervisorsPage';
import { contestReducer, PutContest } from '../../../modules/contestReducer';

describe('ContestSupervisorsPage', () => {
  let wrapper: ReactWrapper<any, any>;
  let contestSupervisorActions: jest.Mocked<any>;

  const response: ContestSupervisorsResponse = {
    data: { page: [], totalCount: 0 },
    profilesMap: {
      userJid1: { username: 'user1' },
      userJid2: { username: 'user2' },
    },
  };

  const render = () => {
    const store = createStore(
      combineReducers({ uriel: combineReducers({ contest: contestReducer }), form: formReducer }),
      applyMiddleware(thunk)
    );
    store.dispatch(PutContest.create(contest));

    const ContestSupervisorsPage = createContestSupervisorsPage(contestSupervisorActions);

    wrapper = mount(
      <IntlProvider locale={navigator.language}>
        <Provider store={store}>
          <MemoryRouter>
            <ContestSupervisorsPage />
          </MemoryRouter>
        </Provider>
      </IntlProvider>
    );
  };

  beforeEach(() => {
    contestSupervisorActions = {
      getSupervisors: jest.fn().mockReturnValue(() => Promise.resolve(response)),
    };
  });

  describe('when there are no supervisors', () => {
    beforeEach(() => {
      render();
    });

    it('shows placeholder text and no supervisors', async () => {
      await new Promise(resolve => setImmediate(resolve));
      wrapper.update();

      expect(wrapper.text()).toContain('No supervisors.');
      expect(wrapper.find('tr')).toHaveLength(0);
    });
  });

  describe('when there are supervisors', () => {
    beforeEach(() => {
      const supervisors: ContestSupervisor[] = [
        {
          userJid: 'userJid1',
        } as ContestSupervisor,
        {
          userJid: 'userJid2',
        } as ContestSupervisor,
      ];
      contestSupervisorActions.getSupervisors.mockReturnValue(() =>
        Promise.resolve({ ...response, data: { page: supervisors, totalCount: 2 } })
      );

      render();
    });

    it('shows the supervisors', async () => {
      await new Promise(resolve => setImmediate(resolve));
      wrapper.update();

      expect(wrapper.find('tr')).toHaveLength(3);
    });
  });
});
