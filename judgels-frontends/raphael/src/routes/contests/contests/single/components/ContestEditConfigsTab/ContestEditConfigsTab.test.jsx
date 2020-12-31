import { mount } from 'enzyme';
import { IntlProvider } from 'react-intl';
import { Provider } from 'react-redux';
import { applyMiddleware, combineReducers, createStore } from 'redux';
import { reducer as formReducer } from 'redux-form';
import thunk from 'redux-thunk';

import { parseDuration } from '../../../../../../utils/duration';
import ContestEditConfigsTab from './ContestEditConfigsTab';
import contestReducer, { PutContest } from '../../../modules/contestReducer';
import * as contestModuleActions from '../../modules/contestModuleActions';

jest.mock('../../modules/contestModuleActions');

describe('ContestEditConfigsTab', () => {
  let wrapper;
  let config;

  const render = () => {
    contestModuleActions.getConfig.mockReturnValue(() => Promise.resolve(config));
    contestModuleActions.upsertConfig.mockReturnValue(() => Promise.resolve({}));

    const store = createStore(
      combineReducers({ uriel: combineReducers({ contest: contestReducer }), form: formReducer }),
      applyMiddleware(thunk)
    );
    store.dispatch(PutContest({ jid: 'contestJid' }));

    wrapper = mount(
      <IntlProvider locale={navigator.language}>
        <Provider store={store}>
          <ContestEditConfigsTab />
        </Provider>
      </IntlProvider>
    );
  };

  describe('form', () => {
    describe('when we fill all fields', () => {
      beforeEach(() => {
        config = {
          icpcStyle: {
            languageRestriction: { allowedLanguageNames: [] },
            wrongSubmissionPenalty: 20,
          },
          scoreboard: {
            isIncognitoScoreboard: true,
          },
          clarificationTimeLimit: {
            clarificationDuration: parseDuration('2h'),
          },
          division: {
            division: 1,
          },
          frozenScoreboard: {
            scoreboardFreezeTime: parseDuration('1h'),
            isOfficialScoreboardAllowed: false,
          },
          externalScoreboard: {
            receiverUrl: 'http://external.scoreboard',
            receiverSecret: 'the_secret',
          },
          virtual: {
            virtualDuration: parseDuration('5h'),
          },
        };
        render();
      });

      it('submits the form', () => {
        const button = wrapper.find('button');
        button.simulate('click');

        wrapper.update();

        const icpcWrongSubmissionPenalty = wrapper.find('input[name="icpcWrongSubmissionPenalty"]');
        icpcWrongSubmissionPenalty.simulate('change', { target: { value: '25' } });

        const scoreboardIsIncognito = wrapper.find('input[name="scoreboardIsIncognito"]');
        scoreboardIsIncognito.simulate('change', { target: { checked: true } });

        const clarificationTimeLimitDuration = wrapper.find('input[name="clarificationTimeLimitDuration"]');
        clarificationTimeLimitDuration.simulate('change', { target: { value: '2h 5m' } });

        const divisionDivision = wrapper.find('input[name="divisionDivision"]');
        divisionDivision.simulate('change', { target: { value: '2' } });

        const frozenScoreboardFreezeTime = wrapper.find('input[name="frozenScoreboardFreezeTime"]');
        frozenScoreboardFreezeTime.simulate('change', { target: { value: '1h 5m' } });

        const frozenScoreboardIsOfficialAllowed = wrapper.find('input[name="frozenScoreboardIsOfficialAllowed"]');
        frozenScoreboardIsOfficialAllowed.simulate('change', { target: { checked: true } });

        const externalScoreboardReceiverUrl = wrapper.find('input[name="externalScoreboardReceiverUrl"]');
        externalScoreboardReceiverUrl.simulate('change', { target: { value: 'http://new.external.scoreboard' } });

        const externalScoreboardReceiverSecret = wrapper.find('input[name="externalScoreboardReceiverSecret"]');
        externalScoreboardReceiverSecret.simulate('change', { target: { value: 'the_new_secret' } });

        const virtualDuration = wrapper.find('input[name="virtualDuration"]');
        virtualDuration.simulate('change', { target: { value: '5h 5m' } });

        const form = wrapper.find('form');
        form.simulate('submit');

        expect(contestModuleActions.upsertConfig).toHaveBeenCalledWith('contestJid', {
          icpcStyle: {
            languageRestriction: { allowedLanguageNames: [] },
            wrongSubmissionPenalty: 25,
          },
          scoreboard: {
            isIncognitoScoreboard: true,
          },
          clarificationTimeLimit: {
            clarificationDuration: 7500000,
          },
          division: {
            division: 2,
          },
          frozenScoreboard: {
            isOfficialScoreboardAllowed: true,
            scoreboardFreezeTime: 3900000,
          },
          externalScoreboard: {
            receiverUrl: 'http://new.external.scoreboard',
            receiverSecret: 'the_new_secret',
          },
          virtual: {
            virtualDuration: 18300000,
          },
        });
      });
    });

    describe('when we allow all languages', () => {
      beforeEach(() => {
        config = {
          icpcStyle: {
            languageRestriction: { allowedLanguageNames: ['C', 'Pascal'] },
            wrongSubmissionPenalty: 20,
          },
          scoreboard: { isIncognitoScoreboard: false },
        };
        render();
      });

      it('submits empty restriction', () => {
        const button = wrapper.find('button');
        button.simulate('click');

        wrapper.update();

        const icpcAllowAllLanguages = wrapper.find('input[name="icpcAllowAllLanguages"]');
        icpcAllowAllLanguages.simulate('change', { target: { checked: true } });

        const form = wrapper.find('form');
        form.simulate('submit');

        expect(contestModuleActions.upsertConfig).toHaveBeenCalledWith('contestJid', {
          icpcStyle: {
            languageRestriction: { allowedLanguageNames: [] },
            wrongSubmissionPenalty: 20,
          },
          scoreboard: { isIncognitoScoreboard: false },
        });
      });
    });

    describe('when we allow not all languages', () => {
      beforeEach(() => {
        config = {
          icpcStyle: {
            languageRestriction: { allowedLanguageNames: [] },
            wrongSubmissionPenalty: 20,
          },
          scoreboard: { isIncognitoScoreboard: false },
        };
        render();
      });

      it('submits the restriction', () => {
        const button = wrapper.find('button');
        button.simulate('click');

        wrapper.update();

        const icpcAllowAllLanguages = wrapper.find('input[name="icpcAllowAllLanguages"]');
        icpcAllowAllLanguages.simulate('change', { target: { checked: false } });

        wrapper.update();

        const icpcAllowedLanguagesPascal = wrapper.find('input[name="icpcAllowedLanguages.Pascal"]');
        icpcAllowedLanguagesPascal.simulate('change', { target: { checked: true } });

        const icpcAllowedLanguagesPython3 = wrapper.find('input[name="icpcAllowedLanguages.Python3"]');
        icpcAllowedLanguagesPython3.simulate('change', { target: { checked: true } });

        const form = wrapper.find('form');
        form.simulate('submit');

        expect(contestModuleActions.upsertConfig).toHaveBeenCalledWith('contestJid', {
          icpcStyle: {
            languageRestriction: { allowedLanguageNames: ['Pascal', 'Python3'] },
            wrongSubmissionPenalty: 20,
          },
          scoreboard: { isIncognitoScoreboard: false },
        });
      });
    });
  });
});
