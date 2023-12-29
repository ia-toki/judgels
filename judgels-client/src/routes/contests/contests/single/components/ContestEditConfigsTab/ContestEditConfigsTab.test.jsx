import { mount } from 'enzyme';
import { Provider } from 'react-redux';
import { applyMiddleware, combineReducers, createStore } from 'redux';
import thunk from 'redux-thunk';

import { parseDuration } from '../../../../../../utils/duration';
import contestReducer, { PutContest } from '../../../modules/contestReducer';
import ContestEditConfigsTab from './ContestEditConfigsTab';

import * as contestModuleActions from '../../modules/contestModuleActions';

jest.mock('../../modules/contestModuleActions');

describe('ContestEditConfigsTab', () => {
  let wrapper;
  let config;

  const render = () => {
    contestModuleActions.getConfig.mockReturnValue(() => Promise.resolve(config));
    contestModuleActions.upsertConfig.mockReturnValue(() => Promise.resolve({}));

    const store = createStore(
      combineReducers({ uriel: combineReducers({ contest: contestReducer }) }),
      applyMiddleware(thunk)
    );
    store.dispatch(PutContest({ jid: 'contestJid' }));

    wrapper = mount(
      <Provider store={store}>
        <ContestEditConfigsTab />
      </Provider>
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
          editorial: {
            preface: '<p>Thank you</p>',
          },
          frozenScoreboard: {
            scoreboardFreezeTime: parseDuration('1h'),
            isOfficialScoreboardAllowed: false,
          },
          mergedScoreboard: {
            previousContestJid: 'JIDCONT00000',
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

        const icpcWrongSubmissionPenalty = wrapper.find('input[name="icpcWrongSubmissionPenalty"]');
        icpcWrongSubmissionPenalty.getDOMNode().value = '25';
        icpcWrongSubmissionPenalty.simulate('input');

        const scoreboardIsIncognito = wrapper.find('input[name="scoreboardIsIncognito"]');
        scoreboardIsIncognito.getDOMNode().checked = true;
        scoreboardIsIncognito.simulate('change');

        const clarificationTimeLimitDuration = wrapper.find('input[name="clarificationTimeLimitDuration"]');
        clarificationTimeLimitDuration.getDOMNode().value = '2h 5m';
        clarificationTimeLimitDuration.simulate('input');

        const divisionDivision = wrapper.find('input[name="divisionDivision"]');
        divisionDivision.getDOMNode().value = '2';
        divisionDivision.simulate('input');

        const frozenScoreboardFreezeTime = wrapper.find('input[name="frozenScoreboardFreezeTime"]');
        frozenScoreboardFreezeTime.getDOMNode().value = '1h 5m';
        frozenScoreboardFreezeTime.simulate('input');

        const frozenScoreboardIsOfficialAllowed = wrapper.find('input[name="frozenScoreboardIsOfficialAllowed"]');
        frozenScoreboardIsOfficialAllowed.getDOMNode().checked = true;
        frozenScoreboardIsOfficialAllowed.simulate('change');

        const mergedScoreboardPreviousContestJid = wrapper.find('input[name="mergedScoreboardPreviousContestJid"]');
        mergedScoreboardPreviousContestJid.getDOMNode().value = 'JIDCONT12345';
        mergedScoreboardPreviousContestJid.simulate('input');

        const externalScoreboardReceiverUrl = wrapper.find('input[name="externalScoreboardReceiverUrl"]');
        externalScoreboardReceiverUrl.getDOMNode().value = 'http://new.external.scoreboard';
        externalScoreboardReceiverUrl.simulate('input');

        const externalScoreboardReceiverSecret = wrapper.find('input[name="externalScoreboardReceiverSecret"]');
        externalScoreboardReceiverSecret.getDOMNode().value = 'the_new_secret';
        externalScoreboardReceiverSecret.simulate('input');

        const virtualDuration = wrapper.find('input[name="virtualDuration"]');
        virtualDuration.getDOMNode().value = '5h 5m';
        virtualDuration.simulate('input');

        const editorialPreface = wrapper.find('textarea[name="editorialPreface"]');
        editorialPreface.getDOMNode().value = '<p>Thank you for your participation.</p>';
        editorialPreface.simulate('input');

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
          mergedScoreboard: {
            previousContestJid: 'JIDCONT12345',
          },
          externalScoreboard: {
            receiverUrl: 'http://new.external.scoreboard',
            receiverSecret: 'the_new_secret',
          },
          virtual: {
            virtualDuration: 18300000,
          },
          editorial: {
            preface: '<p>Thank you for your participation.</p>',
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
        icpcAllowAllLanguages.getDOMNode().checked = true;
        icpcAllowAllLanguages.simulate('change');

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
        icpcAllowAllLanguages.getDOMNode().checked = false;
        icpcAllowAllLanguages.simulate('change');

        wrapper.update();

        const icpcAllowedLanguagesGo = wrapper.find('input[name="icpcAllowedLanguages.Go"]');
        icpcAllowedLanguagesGo.getDOMNode().checked = false;
        icpcAllowedLanguagesGo.simulate('change');

        const icpcAllowedLanguagesPascal = wrapper.find('input[name="icpcAllowedLanguages.Pascal"]');
        icpcAllowedLanguagesPascal.getDOMNode().checked = true;
        icpcAllowedLanguagesPascal.simulate('change');

        const icpcAllowedLanguagesPython3 = wrapper.find('input[name="icpcAllowedLanguages.Python3"]');
        icpcAllowedLanguagesPython3.getDOMNode().checked = true;
        icpcAllowedLanguagesPython3.simulate('change');

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
