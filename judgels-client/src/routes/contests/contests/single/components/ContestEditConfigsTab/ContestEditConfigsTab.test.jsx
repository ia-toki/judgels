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
        icpcWrongSubmissionPenalty.prop('onChange')({ target: { value: '25' } });

        const scoreboardIsIncognito = wrapper.find('input[name="scoreboardIsIncognito"]');
        scoreboardIsIncognito.prop('onChange')({ target: { checked: true } });

        const clarificationTimeLimitDuration = wrapper.find('input[name="clarificationTimeLimitDuration"]');
        clarificationTimeLimitDuration.prop('onChange')({ target: { value: '2h 5m' } });

        const divisionDivision = wrapper.find('input[name="divisionDivision"]');
        divisionDivision.prop('onChange')({ target: { value: '2' } });

        const frozenScoreboardFreezeTime = wrapper.find('input[name="frozenScoreboardFreezeTime"]');
        frozenScoreboardFreezeTime.prop('onChange')({ target: { value: '1h 5m' } });

        const frozenScoreboardIsOfficialAllowed = wrapper.find('input[name="frozenScoreboardIsOfficialAllowed"]');
        frozenScoreboardIsOfficialAllowed.prop('onChange')({ target: { checked: true } });

        const mergedScoreboardPreviousContestJid = wrapper.find('input[name="mergedScoreboardPreviousContestJid"]');
        mergedScoreboardPreviousContestJid.prop('onChange')({ target: { value: 'JIDCONT12345' } });

        const externalScoreboardReceiverUrl = wrapper.find('input[name="externalScoreboardReceiverUrl"]');
        externalScoreboardReceiverUrl.prop('onChange')({ target: { value: 'http://new.external.scoreboard' } });

        const externalScoreboardReceiverSecret = wrapper.find('input[name="externalScoreboardReceiverSecret"]');
        externalScoreboardReceiverSecret.prop('onChange')({ target: { value: 'the_new_secret' } });

        const virtualDuration = wrapper.find('input[name="virtualDuration"]');
        virtualDuration.prop('onChange')({ target: { value: '5h 5m' } });

        const editorialPreface = wrapper.find('textarea[name="editorialPreface"]');
        editorialPreface.prop('onChange')({ target: { value: '<p>Thank you for your participation.</p>' } });

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
