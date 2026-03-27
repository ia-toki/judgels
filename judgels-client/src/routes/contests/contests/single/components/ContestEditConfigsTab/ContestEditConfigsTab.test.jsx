import { act, render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import nock from 'nock';

import { setSession } from '../../../../../../modules/session';
import { QueryClientProviderWrapper } from '../../../../../../test/QueryClientProviderWrapper';
import { TestRouter } from '../../../../../../test/RouterWrapper';
import { parseDuration } from '../../../../../../utils/duration';
import { nockUriel } from '../../../../../../utils/nock';
import ContestEditConfigsTab from './ContestEditConfigsTab';

describe('ContestEditConfigsTab', () => {
  beforeEach(() => {
    setSession('token', { jid: 'userJid' });
  });

  const renderComponent = async ({
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
    },
  } = {}) => {
    nockUriel().get('/contests/slug/contest-slug').reply(200, {
      jid: 'contestJid',
      slug: 'contest-slug',
    });

    nockUriel().get('/contests/contestJid/modules/config').reply(200, config);

    await act(async () =>
      render(
        <QueryClientProviderWrapper>
          <TestRouter initialEntries={['/contests/contest-slug']} path="/contests/$contestSlug">
            <ContestEditConfigsTab />
          </TestRouter>
        </QueryClientProviderWrapper>
      )
    );
  };

  test('submits the form when we fill all fields', async () => {
    await renderComponent();

    const user = userEvent.setup();

    const button = await screen.findByRole('button', { name: /edit/i });
    await user.click(button);

    const icpcWrongSubmissionPenalty = screen.getByRole('textbox', { name: /wrong submission penalty/i });
    await user.clear(icpcWrongSubmissionPenalty);
    await user.type(icpcWrongSubmissionPenalty, '25');

    const scoreboardIsIncognito = screen.getByRole('checkbox', { name: /incognito scoreboard/i });
    await user.click(scoreboardIsIncognito);

    const clarificationTimeLimitDuration = screen.getByRole('textbox', { name: /clarification duration/i });
    await user.clear(clarificationTimeLimitDuration);
    await user.type(clarificationTimeLimitDuration, '2h 5m');

    const divisionDivision = screen.getByRole('textbox', { name: /division/i });
    await user.clear(divisionDivision);
    await user.type(divisionDivision, '2');

    const frozenScoreboardFreezeTime = screen.getByRole('textbox', { name: /freeze time/i });
    await user.clear(frozenScoreboardFreezeTime);
    await user.type(frozenScoreboardFreezeTime, '1h 5m');

    const frozenScoreboardIsOfficialAllowed = screen.getByRole('checkbox', { name: /is now unfrozen/i });
    await user.click(frozenScoreboardIsOfficialAllowed);

    const mergedScoreboardPreviousContestJid = screen.getByRole('textbox', { name: /previous contest jid/i });
    await user.clear(mergedScoreboardPreviousContestJid);
    await user.type(mergedScoreboardPreviousContestJid, 'JIDCONT12345');

    const externalScoreboardReceiverUrl = screen.getByRole('textbox', { name: /receiver url/i });
    await user.clear(externalScoreboardReceiverUrl);
    await user.type(externalScoreboardReceiverUrl, 'http://new.external.scoreboard');

    const externalScoreboardReceiverSecret = screen.getByRole('textbox', { name: /receiver secret/i });
    await user.clear(externalScoreboardReceiverSecret);
    await user.type(externalScoreboardReceiverSecret, 'the_new_secret');

    const virtualDuration = screen.getByRole('textbox', { name: /virtual contest duration/i });
    await user.clear(virtualDuration);
    await user.type(virtualDuration, '5h 5m');

    const editorialPreface = screen.getByRole('textbox', { name: /preface/i });
    await user.clear(editorialPreface);
    await user.type(editorialPreface, '<p>Thank you for your participation.</p>');

    nockUriel()
      .put('/contests/contestJid/modules/config', {
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
      })
      .reply(200);

    const submitButton = screen.getByRole('button', { name: /save/i });
    await user.click(submitButton);

    await waitFor(() => expect(nock.isDone()).toBe(true));
  });

  test('submits empty restriction when we allow all languages', async () => {
    await renderComponent({
      config: {
        icpcStyle: {
          languageRestriction: { allowedLanguageNames: ['C', 'Pascal'] },
          wrongSubmissionPenalty: 20,
        },
        scoreboard: { isIncognitoScoreboard: false },
      },
    });

    const user = userEvent.setup();

    const button = await screen.findByRole('button', { name: /edit/i });
    await user.click(button);

    const icpcAllowAllLanguages = screen.getByRole('checkbox', { name: /\(all\)/i });
    await user.click(icpcAllowAllLanguages);

    nockUriel()
      .put('/contests/contestJid/modules/config', {
        icpcStyle: {
          languageRestriction: { allowedLanguageNames: [] },
          wrongSubmissionPenalty: 20,
        },
        scoreboard: { isIncognitoScoreboard: false },
      })
      .reply(200);

    const submitButton = screen.getByRole('button', { name: /save/i });
    await user.click(submitButton);

    await waitFor(() => expect(nock.isDone()).toBe(true));
  });

  test('submits the restriction when we allow not all languages', async () => {
    await renderComponent({
      config: {
        icpcStyle: {
          languageRestriction: { allowedLanguageNames: [] },
          wrongSubmissionPenalty: 20,
        },
        scoreboard: { isIncognitoScoreboard: false },
      },
    });

    const user = userEvent.setup();

    const button = await screen.findByRole('button', { name: /edit/i });
    await user.click(button);

    const icpcAllowAllLanguages = screen.getByRole('checkbox', { name: /\(all\)/i });
    await user.click(icpcAllowAllLanguages);

    const icpcAllowedLanguagesPascal = screen.getByRole('checkbox', { name: /pascal/i });
    await user.click(icpcAllowedLanguagesPascal);

    const icpcAllowedLanguagesPython3 = screen.getByRole('checkbox', { name: /python 3/i });
    await user.click(icpcAllowedLanguagesPython3);

    nockUriel()
      .put('/contests/contestJid/modules/config', {
        icpcStyle: {
          languageRestriction: { allowedLanguageNames: ['Pascal', 'Python3'] },
          wrongSubmissionPenalty: 20,
        },
        scoreboard: { isIncognitoScoreboard: false },
      })
      .reply(200);

    const submitButton = screen.getByRole('button', { name: /save/i });
    await user.click(submitButton);

    await waitFor(() => expect(nock.isDone()).toBe(true));
  });
});
