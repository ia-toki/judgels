import { mount } from 'enzyme';
import { IntlProvider } from 'react-intl';
import { Provider } from 'react-redux';
import { MemoryRouter } from 'react-router';
import { applyMiddleware, combineReducers, createStore } from 'redux';
import thunk from 'redux-thunk';

import ContestSubmissionsPage from './ContestSubmissionsPage';
import contestReducer, { PutContest } from '../../../../modules/contestReducer';
import * as contestSubmissionActions from '../modules/contestSubmissionActions';

jest.mock('../modules/contestSubmissionActions');

describe('ContestSubmissionsPage', () => {
  let wrapper;
  let submissions;
  let canSupervise;
  let canManage;

  const render = async () => {
    contestSubmissionActions.getSubmissions.mockReturnValue(() =>
      Promise.resolve({
        data: { page: submissions },
        config: {
          canSupervise,
          canManage,
          userJids: [],
          problemJids: [],
        },
        profilesMap: {
          userJid1: { username: 'user1' },
          userJid2: { username: 'user2' },
        },
        problemAliasesMap: {
          problemJid1: 'A',
          problemJid2: 'B',
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
            <ContestSubmissionsPage />
          </MemoryRouter>
        </Provider>
      </IntlProvider>
    );

    await new Promise(resolve => setImmediate(resolve));
    wrapper.update();
  };

  describe('when there are no submissions', () => {
    beforeEach(async () => {
      submissions = [];
      await render();
    });

    it('shows placeholder text and no submissions', () => {
      expect(wrapper.text()).toContain('No submissions.');
      expect(wrapper.find('tr')).toHaveLength(0);
    });
  });

  describe('when there are submissions', () => {
    beforeEach(() => {
      submissions = [
        {
          id: 20,
          jid: 'jid1',
          userJid: 'userJid1',
          problemJid: 'problemJid1',
          containerJid: 'contestJid',
          gradingEngine: 'Batch',
          gradingLanguage: 'Cpp17',
          time: new Date(new Date().setDate(new Date().getDate() - 1)).getTime(),
        },
        {
          id: 10,
          jid: 'jid2',
          userJid: 'userJid2',
          problemJid: 'problemJid2',
          containerJid: 'contestJid',
          gradingEngine: 'Batch',
          gradingLanguage: 'Cpp17',
          time: new Date(new Date().setDate(new Date().getDate() - 2)).getTime(),
          latestGrading: {
            verdict: { code: 'WA' },
            score: 70,
          },
        },
      ];
    });

    describe('when not canSupervise', () => {
      beforeEach(async () => {
        canSupervise = false;
        canManage = false;
        await render();
      });

      it('shows the submissions', () => {
        expect(wrapper.find('tr').map(tr => tr.find('td').map(td => td.text().trim()))).toEqual([
          [],
          ['20', 'A', 'C++17', '', '', '1 day ago', 'search'],
          ['10', 'B', 'C++17', 'WA', '70', '2 days ago', 'search'],
        ]);
      });
    });

    describe('when canSupervise', () => {
      beforeEach(async () => {
        canSupervise = true;
        canManage = false;
        await render();
      });

      it('shows the submissions', () => {
        expect(wrapper.find('tr').map(tr => tr.find('td').map(td => td.text().trim()))).toEqual([
          [],
          ['20', 'user1', 'A', 'C++17', '', '', '1 day ago', 'search'],
          ['10', 'user2', 'B', 'C++17', 'WA', '70', '2 days ago', 'search'],
        ]);
      });
    });

    describe('when canManage', () => {
      beforeEach(async () => {
        canSupervise = true;
        canManage = true;
        await render();
      });

      it('shows the submissions', () => {
        expect(
          wrapper.find('tr').map(tr =>
            tr.find('td').map(td =>
              td
                .text()
                .replace(/\s+/g, ' ')
                .trim()
            )
          )
        ).toEqual([
          [],
          ['20 refresh', 'user1', 'A', 'C++17', '', '', '1 day ago', 'search'],
          ['10 refresh', 'user2', 'B', 'C++17', 'WA', '70', '2 days ago', 'search'],
        ]);
      });
    });
  });
});
