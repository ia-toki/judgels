import { mount, ReactWrapper } from 'enzyme';
import * as React from 'react';
import { IntlProvider } from 'react-intl';
import { Provider } from 'react-redux';
import { MemoryRouter } from 'react-router';
import { applyMiddleware, combineReducers, createStore } from 'redux';
import { reducer as formReducer } from 'redux-form';
import thunk from 'redux-thunk';

import { ContestModulesConfig } from 'modules/api/uriel/contestModule';
import { contest, contestJid } from 'fixtures/state';
import { parseDuration } from 'utils/duration';

import { createContestEditConfigsTab } from './ContestEditConfigsTab';
import { contestReducer, PutContest } from '../../../modules/contestReducer';

describe('ContestEditConfigsTab', () => {
  let contestModuleActions: jest.Mocked<any>;
  let wrapper: ReactWrapper<any, any>;
  let config: ContestModulesConfig;

  const render = () => {
    contestModuleActions = {
      getConfig: jest.fn().mockReturnValue(() => Promise.resolve(config)),
      upsertConfig: jest.fn().mockReturnValue(() => Promise.resolve({})),
    };

    const ContestEditConfigsTab = createContestEditConfigsTab(contestModuleActions);

    const store = createStore(
      combineReducers({ uriel: combineReducers({ contest: contestReducer }), form: formReducer }),
      applyMiddleware(thunk)
    );
    store.dispatch(PutContest.create(contest));

    wrapper = mount(
      <IntlProvider locale={navigator.language}>
        <Provider store={store}>
          <MemoryRouter>
            <ContestEditConfigsTab />
          </MemoryRouter>
        </Provider>
      </IntlProvider>
    );
  };

  describe('contest edit configs tab form', () => {
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
          delayedGrading: {
            delayDuration: parseDuration('10m'),
          },
          frozenScoreboard: {
            scoreboardFreezeTime: parseDuration('1h'),
            isOfficialScoreboardAllowed: false,
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

        const delayedGradingDuration = wrapper.find('input[name="delayedGradingDuration"]');
        delayedGradingDuration.simulate('change', { target: { value: '10m 5s' } });

        const frozenScoreboardFreezeTime = wrapper.find('input[name="frozenScoreboardFreezeTime"]');
        frozenScoreboardFreezeTime.simulate('change', { target: { value: '1h 5m' } });

        const frozenScoreboardIsOfficialAllowed = wrapper.find('input[name="frozenScoreboardIsOfficialAllowed"]');
        frozenScoreboardIsOfficialAllowed.simulate('change', { target: { checked: true } });

        const virtualDuration = wrapper.find('input[name="virtualDuration"]');
        virtualDuration.simulate('change', { target: { value: '5h 5m' } });

        const form = wrapper.find('form');
        form.simulate('submit');

        expect(contestModuleActions.upsertConfig).toHaveBeenCalledWith(contestJid, {
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
          delayedGrading: {
            delayDuration: 605000,
          },
          frozenScoreboard: {
            isOfficialScoreboardAllowed: true,
            scoreboardFreezeTime: 3900000,
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

        expect(contestModuleActions.upsertConfig).toHaveBeenCalledWith(contestJid, {
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

        expect(contestModuleActions.upsertConfig).toHaveBeenCalledWith(contestJid, {
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
