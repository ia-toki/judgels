import { mount } from 'enzyme';
import { Provider } from 'react-redux';
import { MemoryRouter } from 'react-router';
import { applyMiddleware, combineReducers, createStore } from 'redux';
import thunk from 'redux-thunk';

import { ProblemType } from '../../../../../../modules/api/sandalphon/problem';
import sessionReducer, { PutUser } from '../../../../../../modules/session/sessionReducer';
import webPrefsReducer, { PutEditorialLanguage } from '../../../../../../modules/webPrefs/webPrefsReducer';
import contestReducer, { PutContest } from '../../../modules/contestReducer';
import ContestEditorialPage from './ContestEditorialPage';

import * as contestEditorialActions from '../modules/contestEditorialActions';

jest.mock('../modules/contestEditorialActions');

describe('ContestEditorialPage', () => {
  let wrapper;
  let problems;
  let canManage;

  const render = async () => {
    contestEditorialActions.getEditorial.mockReturnValue(() =>
      Promise.resolve({
        preface: '<p>Thanks for participating.</p>',
        problems: [
          {
            problemJid: 'problemJid1',
            alias: 'A',
            status: 'OPEN',
          },
          {
            problemJid: 'problemJid2',
            alias: 'B',
            status: 'OPEN',
          },
          {
            problemJid: 'problemJid3',
            alias: 'C',
            status: 'OPEN',
          },
        ],
        problemsMap: {
          problemJid1: {
            slug: 'problem-a',
            type: ProblemType.PROGRAMMING,
            defaultLanguage: 'id',
            titlesByLanguage: {
              id: 'Soal A',
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
          problemJid3: {
            slug: 'problem-c',
            type: ProblemType.PROGRAMMING,
            defaultLanguage: 'en',
            titlesByLanguage: {
              id: 'Soal C',
              en: 'Problem C',
            },
          },
        },
        problemEditorialsMap: {
          problemJid1: {
            text: '<p>Hello. This is editorial for problem A</p>',
            languages: ['id'],
          },
          problemJid2: {
            text: '<p>Hello. This is editorial for problem B</p>',
            languages: ['en', 'id'],
          },
        },
        problemMetadatasMap: {
          problemJid1: {
            hasEditorial: true,
            settersMap: {},
          },
          problemJid2: {
            hasEditorial: true,
            settersMap: {},
          },
          problemJid3: {
            hasEditorial: false,
            settersMap: {},
          },
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
    store.dispatch(PutEditorialLanguage('en'));

    wrapper = mount(
      <Provider store={store}>
        <MemoryRouter>
          <ContestEditorialPage />
        </MemoryRouter>
      </Provider>
    );

    await new Promise(resolve => setImmediate(resolve));
    await new Promise(resolve => setImmediate(resolve));
    wrapper.update();
  };

  describe('content', () => {
    beforeEach(async () => {
      await render();
    });

    it('shows the editorial', () => {
      const text = wrapper.find('.contest-editorial').text();
      expect(text).toEqual(
        '' +
          'Thanks for participating.' +
          'A. Soal AHello. This is editorial for problem A' +
          'B. Problem BHello. This is editorial for problem B'
      );
    });
  });
});
