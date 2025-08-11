import { mount } from 'enzyme';
import { Provider } from 'react-redux';
import { MemoryRouter } from 'react-router';
import { applyMiddleware, combineReducers, createStore } from 'redux';
import thunk from 'redux-thunk';

import { ProblemType } from '../../../../../../modules/api/sandalphon/problem';
import { ContestProblemStatus } from '../../../../../../modules/api/uriel/contestProblem';
import sessionReducer, { PutUser } from '../../../../../../modules/session/sessionReducer';
import webPrefsReducer, { PutStatementLanguage } from '../../../../../../modules/webPrefs/webPrefsReducer';
import contestReducer, { PutContest } from '../../../modules/contestReducer';
import ContestProblemsPage from './ContestProblemsPage';

import * as contestProblemActions from '../modules/contestProblemActions';

jest.mock('../modules/contestProblemActions');

describe('ContestProblemsPage', () => {
  let wrapper;
  let problems;
  let canManage;

  const render = async () => {
    contestProblemActions.getProblems.mockReturnValue(() =>
      Promise.resolve({
        data: problems,
        problemsMap: {
          problemJid1: {
            slug: 'problem-a',
            type: ProblemType.PROGRAMMING,
            defaultLanguage: 'id',
            titlesByLanguage: {
              id: 'Soal A',
              en: 'Problem A',
            },
          },
          problemJid2: {
            slug: 'problem-b',
            type: ProblemType.PROGRAMMING,
            defaultLanguage: 'id',
            titlesByLanguage: {
              id: 'Soal B',
              en: 'Problem B',
            },
          },
        },
        totalSubmissionsMap: { problemJid1: 0, problemJid2: 2 },
        config: {
          canManage,
        },
      })
    );

    const store = createStore(
      combineReducers({
        session: sessionReducer,
        webPrefs: webPrefsReducer,
        uriel: combineReducers({ contest: contestReducer }),
      }),
      applyMiddleware(thunk)
    );
    store.dispatch(PutUser({ jid: 'userJid' }));
    store.dispatch(PutContest({ jid: 'contestJid' }));
    store.dispatch(PutStatementLanguage('en'));

    wrapper = mount(
      <Provider store={store}>
        <MemoryRouter>
          <ContestProblemsPage />
        </MemoryRouter>
      </Provider>
    );

    await new Promise(resolve => setImmediate(resolve));
    await new Promise(resolve => setImmediate(resolve));
    wrapper.update();
  };

  describe('action buttons', () => {
    beforeEach(() => {
      problems = [];
    });

    describe('when not canManage', () => {
      beforeEach(async () => {
        canManage = false;
        await render();
      });

      it('shows no buttons', () => {
        expect(wrapper.find('div.content-card__section').find('button')).toHaveLength(0);
      });
    });

    describe('when canManage', () => {
      beforeEach(async () => {
        canManage = true;
        await render();
      });

      it('shows action buttons', () => {
        expect(
          wrapper
            .find('div.content-card__section')
            .find('button')
            .map(b => b.text())
        ).toEqual(['Edit problems']);
      });
    });
  });

  describe('content', () => {
    describe('when there are no problems', () => {
      beforeEach(async () => {
        problems = [];
        await render();
      });

      it('shows placeholder text and no problems', () => {
        expect(wrapper.text()).toContain('No problems.');
        expect(wrapper.find('div.contest-problem-card')).toHaveLength(0);
      });
    });

    describe('when there are problems', () => {
      beforeEach(async () => {
        problems = [
          {
            problemJid: 'problemJid1',
            alias: 'A',
            status: 'CLOSED',
            submissionsLimit: null,
          },
          {
            problemJid: 'problemJid2',
            alias: 'B',
            status: 'OPEN',
            submissionsLimit: 10,
            points: 100,
          },
        ];
        await render();
      });

      it('shows the problems', () => {
        const cards = wrapper.find('div.contest-problem-card');
        expect(cards.map(card => card.text())).toEqual([
          'B. Problem B [100 points]8 submissions left',
          'A. Problem ACLOSED',
        ]);
      });
    });
  });
});
