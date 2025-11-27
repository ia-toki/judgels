import { act, render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { Provider } from 'react-redux';
import { applyMiddleware, combineReducers, createStore } from 'redux';
import thunk from 'redux-thunk';

import { parseDuration } from '../../../../../../utils/duration';
import contestReducer, { PutContest } from '../../../modules/contestReducer';
import ContestEditConfigsTab from './ContestEditConfigsTab';

import * as contestModuleActions from '../../modules/contestModuleActions';

jest.mock('../../modules/contestModuleActions');

describe('ContestEditConfigsTab', () => {
  let config;

  const renderComponent = async () => {
    contestModuleActions.getConfig.mockReturnValue(() => Promise.resolve(config));
    contestModuleActions.upsertConfig.mockReturnValue(() => Promise.resolve({}));

    const store = createStore(
      combineReducers({ uriel: combineReducers({ contest: contestReducer }) }),
      applyMiddleware(thunk)
    );
    store.dispatch(PutContest({ jid: 'contestJid' }));

    await act(async () =>
      render(
        <Provider store={store}>
          <ContestEditConfigsTab />
        </Provider>
      )
    );
  };

  describe('form', () => {
    describe('when we fill all fields', () => {
      beforeEach(async () => {
        config = {
          icpcStyle: {
            languageRestriction: { allowedLanguageNames: [] },
            wrongSubmissionPenalty: 20,
          },
          scoreboard: {
            isIncognitoScoreboard: false,
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
        await renderComponent();
      });

      it('submits the form', async () => {
        const user = userEvent.setup();

        const button = screen.getByRole('button', { name: /edit/i });
        await user.click(button);

        const icpcWrongSubmissionPenalty = document.querySelector('input[name="icpcWrongSubmissionPenalty"]');
        await user.clear(icpcWrongSubmissionPenalty);
        await user.type(icpcWrongSubmissionPenalty, '25');

        const scoreboardIsIncognito = document.querySelector('input[name="scoreboardIsIncognito"]');
        await user.click(scoreboardIsIncognito);

        const clarificationTimeLimitDuration = document.querySelector('input[name="clarificationTimeLimitDuration"]');
        await user.clear(clarificationTimeLimitDuration);
        await user.type(clarificationTimeLimitDuration, '2h 5m');

        const divisionDivision = document.querySelector('input[name="divisionDivision"]');
        await user.clear(divisionDivision);
        await user.type(divisionDivision, '2');

        const frozenScoreboardFreezeTime = document.querySelector('input[name="frozenScoreboardFreezeTime"]');
        await user.clear(frozenScoreboardFreezeTime);
        await user.type(frozenScoreboardFreezeTime, '1h 5m');

        const frozenScoreboardIsOfficialAllowed = document.querySelector(
          'input[name="frozenScoreboardIsOfficialAllowed"]'
        );
        await user.click(frozenScoreboardIsOfficialAllowed);

        const mergedScoreboardPreviousContestJid = document.querySelector(
          'input[name="mergedScoreboardPreviousContestJid"]'
        );
        await user.clear(mergedScoreboardPreviousContestJid);
        await user.type(mergedScoreboardPreviousContestJid, 'JIDCONT12345');

        const externalScoreboardReceiverUrl = document.querySelector('input[name="externalScoreboardReceiverUrl"]');
        await user.clear(externalScoreboardReceiverUrl);
        await user.type(externalScoreboardReceiverUrl, 'http://new.external.scoreboard');

        const externalScoreboardReceiverSecret = document.querySelector(
          'input[name="externalScoreboardReceiverSecret"]'
        );
        await user.clear(externalScoreboardReceiverSecret);
        await user.type(externalScoreboardReceiverSecret, 'the_new_secret');

        const virtualDuration = document.querySelector('input[name="virtualDuration"]');
        await user.clear(virtualDuration);
        await user.type(virtualDuration, '5h 5m');

        const editorialPreface = document.querySelector('textarea[name="editorialPreface"]');
        await user.clear(editorialPreface);
        await user.type(editorialPreface, '<p>Thank you for your participation.</p>');

        const submitButton = screen.getByRole('button', { name: /save/i });
        await user.click(submitButton);

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
      beforeEach(async () => {
        config = {
          icpcStyle: {
            languageRestriction: { allowedLanguageNames: ['C', 'Pascal'] },
            wrongSubmissionPenalty: 20,
          },
          scoreboard: { isIncognitoScoreboard: false },
        };
        await renderComponent();
      });

      it('submits empty restriction', async () => {
        const user = userEvent.setup();

        const button = screen.getByRole('button', { name: /edit/i });
        await user.click(button);

        const icpcAllowAllLanguages = document.querySelector('input[name="icpcAllowAllLanguages"]');
        await user.click(icpcAllowAllLanguages);

        const submitButton = screen.getByRole('button', { name: /save/i });
        await user.click(submitButton);

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
      beforeEach(async () => {
        config = {
          icpcStyle: {
            languageRestriction: { allowedLanguageNames: [] },
            wrongSubmissionPenalty: 20,
          },
          scoreboard: { isIncognitoScoreboard: false },
        };
        await renderComponent();
      });

      it('submits the restriction', async () => {
        const user = userEvent.setup();

        const button = screen.getByRole('button', { name: /edit/i });
        await user.click(button);

        const icpcAllowAllLanguages = document.querySelector('input[name="icpcAllowAllLanguages"]');
        await user.click(icpcAllowAllLanguages);

        const icpcAllowedLanguagesPascal = screen.getByRole('checkbox', { name: /pascal/i });
        await user.click(icpcAllowedLanguagesPascal);

        const icpcAllowedLanguagesPython3 = screen.getByRole('checkbox', { name: /python 3/i });
        await user.click(icpcAllowedLanguagesPython3);

        const submitButton = screen.getByRole('button', { name: /save/i });
        await user.click(submitButton);

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
