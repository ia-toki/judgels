import { mount } from 'enzyme';
import { IntlProvider } from 'react-intl';
import { Provider } from 'react-redux';
import { MemoryRouter } from 'react-router';
import { applyMiddleware, combineReducers, createStore } from 'redux';
import thunk from 'redux-thunk';

import sessionReducer, { PutUser } from '../../../../../../modules/session/sessionReducer';
import webPrefsReducer, { PutStatementLanguage } from '../../../../../../modules/webPrefs/webPrefsReducer';

import ContestClarificationsPage from './ContestClarificationsPage';
import { ContestClarificationStatus } from '../../../../../../modules/api/uriel/contestClarification';
import contestReducer, { PutContest } from '../../../modules/contestReducer';
import * as contestClarificationActions from '../modules/contestClarificationActions';

jest.mock('../modules/contestClarificationActions');

describe('ContestClarificationsPage', () => {
  let wrapper;
  let clarifications;
  let canCreate;
  let canSupervise;

  const render = async () => {
    contestClarificationActions.getClarifications.mockReturnValue(() =>
      Promise.resolve({
        data: {
          page: clarifications,
        },
        config: {
          canCreate,
          canSupervise,
          canManage: true,
          problemJids: ['problemJid1', 'problemJid2'],
        },
        profilesMap: {
          userJid1: { username: 'username1' },
          userJid2: { username: 'username2' },
          userJid3: { username: 'username3' },
        },
        problemAliasesMap: { problemJid1: 'A', problemJid2: 'B' },
        problemNamesMap: { problemJid1: 'Problem 1', problemJid2: 'Problem 2' },
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
      <IntlProvider locale={navigator.language}>
        <Provider store={store}>
          <MemoryRouter>
            <ContestClarificationsPage />
          </MemoryRouter>
        </Provider>
      </IntlProvider>
    );

    await new Promise(resolve => setImmediate(resolve));
    wrapper.update();
  };

  describe('action buttons', () => {
    beforeEach(() => {
      clarifications = [];
    });

    describe('when not canCreate', () => {
      beforeEach(async () => {
        canCreate = false;
        await render();
      });

      it('shows no buttons', () => {
        expect(wrapper.find('button')).toHaveLength(0);
      });
    });

    describe('when canCreate', () => {
      beforeEach(async () => {
        canCreate = true;
        await render();
      });

      it('shows action buttons', () => {
        expect(wrapper.find('button').map(b => b.text())).toEqual(['plusNew clarification']);
      });
    });
  });

  describe('content', () => {
    describe('when there are no clarifications', () => {
      beforeEach(async () => {
        clarifications = [];
        await render();
      });

      it('shows placeholder text and no clarifications', () => {
        expect(wrapper.text()).toContain('No clarifications.');
        expect(wrapper.find('div.contest-clarification-card')).toHaveLength(0);
      });
    });

    describe('when there are clarifications', () => {
      beforeEach(() => {
        clarifications = [
          {
            jid: 'clarificationJid1',
            userJid: 'userJid1',
            topicJid: 'contestJid',
            title: 'Title 1',
            question: 'Question 1',
            status: ContestClarificationStatus.Answered,
            answer: 'Answer 1',
            answererJid: 'userJid3',
            time: new Date(new Date().setDate(new Date().getDate() - 2)).getTime(),
            answeredTime: new Date(new Date().setDate(new Date().getDate() - 1)).getTime(),
          },
          {
            jid: 'clarificationJid2',
            userJid: 'userJid2',
            topicJid: 'problemJid1',
            title: 'Title 2',
            question: 'Question 2',
            status: ContestClarificationStatus.Asked,
            time: new Date(new Date().setDate(new Date().getDate() - 1)).getTime(),
          },
        ];
      });

      describe('when not canSupervise', () => {
        beforeEach(async () => {
          canSupervise = false;
          await render();
        });

        it('shows the clarifications', () => {
          const cards = wrapper.find('div.contest-clarification-card');
          expect(
            cards.map(card => [
              card
                .find('h4')
                .at(0)
                .map(n => n.text().replace(/\s+/g, ' ')),
              card
                .find('.contest-clarification-card__info')
                .at(0)
                .map(n => n.text().replace(/\s+/g, ' ')),
              card
                .find('.multiline-text')
                .at(0)
                .map(n => n.text()),
            ])
          ).toEqual([
            [['Title 1 General'], ['asked 2 days ago'], ['Question 1']],
            [['Answer:'], ['answered 1 day ago'], ['Answer 1']],
            [['Title 2 A. Problem 1'], ['asked 1 day ago'], ['Question 2']],
            [[], [], []],
          ]);
        });
      });

      describe('when canSupervise', () => {
        beforeEach(async () => {
          canSupervise = true;
          await render();
        });

        it('shows the clarifications', () => {
          const cards = wrapper.find('div.contest-clarification-card');
          expect(
            cards.map(card => [
              card
                .find('h4')
                .at(0)
                .map(n => n.text().replace(/\s+/g, ' ')),
              card
                .find('.contest-clarification-card__info')
                .at(0)
                .map(n => n.text().replace(/\s+/g, ' ')),
              card
                .find('.multiline-text')
                .at(0)
                .map(n => n.text()),
            ])
          ).toEqual([
            [['Title 1 General'], ['asked 2 days ago by username1'], ['Question 1']],
            [['Answer:'], ['answered 1 day ago by username3'], ['Answer 1']],
            [['Title 2 A. Problem 1'], ['asked 1 day ago by username2'], ['Question 2']],
            [[], [], []],
          ]);
        });
      });
    });
  });
});
