import { mount } from 'enzyme';
import { IntlProvider } from 'react-intl';
import { Provider } from 'react-redux';
import { MemoryRouter } from 'react-router';
import { applyMiddleware, combineReducers, createStore } from 'redux';
import thunk from 'redux-thunk';

import ContestSupervisorsPage from './ContestSupervisorsPage';
import contestReducer, { PutContest } from '../../../modules/contestReducer';
import * as contestSupervisorActions from '../../modules/contestSupervisorActions';

jest.mock('../../modules/contestSupervisorActions');

describe('ContestSupervisorsPage', () => {
  let wrapper;
  let supervisors;

  const render = async () => {
    contestSupervisorActions.getSupervisors.mockReturnValue(() =>
      Promise.resolve({
        data: {
          page: supervisors,
        },
        profilesMap: {
          userJid1: { username: 'username1' },
          userJid2: { username: 'username2' },
        },
      })
    );

    const store = createStore(
      combineReducers({ uriel: combineReducers({ contest: contestReducer }) }),
      applyMiddleware(thunk)
    );
    store.dispatch(PutContest({ jid: 'contestJid' }));

    wrapper = mount(
      <IntlProvider locale={navigator.language}>
        <Provider store={store}>
          <MemoryRouter>
            <ContestSupervisorsPage />
          </MemoryRouter>
        </Provider>
      </IntlProvider>
    );

    await new Promise(resolve => setImmediate(resolve));
    wrapper.update();
  };

  describe('action buttons', () => {
    beforeEach(async () => {
      supervisors = [];
      await render();
    });

    it('shows action buttons', () => {
      expect(wrapper.find('button').map(b => b.text())).toEqual([
        'plusAdd/update supervisors',
        'trashRemove supervisors',
      ]);
    });
  });

  describe('content', () => {
    describe('when there are no supervisors', () => {
      beforeEach(async () => {
        supervisors = [];
        await render();
      });

      it('shows placeholder text and no supervisors', () => {
        expect(wrapper.text()).toContain('No supervisors.');
        expect(wrapper.find('tr')).toHaveLength(0);
      });
    });

    describe('when there are supervisors', () => {
      beforeEach(async () => {
        supervisors = [
          {
            userJid: 'userJid1',
            managementPermissions: ['ANNOUNCEMENT', 'PROBLEM'],
          },
          {
            userJid: 'userJid2',
            managementPermissions: ['ALL'],
          },
        ];
        await render();
      });

      it('shows the supervisors', () => {
        expect(wrapper.find('tr').map(tr => tr.find('td').map(td => td.text()))).toEqual([
          [],
          ['username1', 'notificationsANNCmanualPROB'],
          ['username2', 'ninjaALL'],
        ]);
      });
    });
  });
});
