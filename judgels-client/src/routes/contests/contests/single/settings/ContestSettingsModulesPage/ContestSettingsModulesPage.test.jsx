import { act, render, screen, waitFor, within } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import nock from 'nock';

import { setSession } from '../../../../../../modules/session';
import { QueryClientProviderWrapper } from '../../../../../../test/QueryClientProviderWrapper';
import { TestRouter } from '../../../../../../test/RouterWrapper';
import { parseDuration } from '../../../../../../utils/duration';
import { nockApi } from '../../../../../../utils/nock';
import ContestSettingsModulesPage from './ContestSettingsModulesPage';

describe('ContestSettingsModulesPage', () => {
  beforeEach(() => {
    setSession('token', { jid: 'userJid' });
  });

  const renderComponent = async ({
    modules = ['CLARIFICATION', 'CLARIFICATION_TIME_LIMIT'],
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
    },
  } = {}) => {
    nockApi().get('/v2/contests/slug/contest-slug').reply(200, {
      jid: 'contestJid',
      slug: 'contest-slug',
      style: 'ICPC',
    });

    nockApi().get('/v2/contests/contestJid/modules').reply(200, modules);
    nockApi().get('/v2/contests/contestJid/modules/config').reply(200, config);

    await act(async () =>
      render(
        <QueryClientProviderWrapper>
          <TestRouter
            initialEntries={['/contests/contest-slug/settings/modules']}
            path="/contests/$contestSlug/settings/modules"
          >
            <ContestSettingsModulesPage />
          </TestRouter>
        </QueryClientProviderWrapper>
      )
    );
  };

  const findModule = async title => {
    const heading = await screen.findByRole('heading', { level: 5, name: title });
    return heading.closest('.contest-module');
  };

  test('renders the enabled modules first', async () => {
    await renderComponent();

    const modules = await screen.findAllByRole('heading', { level: 5 });
    expect(modules.map(h5 => h5.textContent)).toEqual([
      'ICPC style',
      'Scoreboard',
      'Clarification',
      'Clarification time limit',
      'Registration',
      'Division',
      'Editorial',
      'Freezable scoreboard',
      'Merged scoreboard',
      'External scoreboard',
      'Virtual contest',
      'File',
      'Paused',
      'Hidden',
    ]);
  });

  test('saves the configs of all enabled modules together', async () => {
    await renderComponent({
      modules: [
        'CLARIFICATION_TIME_LIMIT',
        'DIVISION',
        'EDITORIAL',
        'FROZEN_SCOREBOARD',
        'MERGED_SCOREBOARD',
        'EXTERNAL_SCOREBOARD',
        'VIRTUAL',
      ],
      config: {
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
          allowHigherDivisionsUnofficially: false,
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
    });

    const user = userEvent.setup();

    await user.click(await screen.findByRole('button', { name: /edit/i }));

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

    const divisionAllowHigherDivisionsUnofficially = screen.getByRole('checkbox', {
      name: /allow higher divisions to participate unofficially/i,
    });
    await user.click(divisionAllowHigherDivisionsUnofficially);

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

    nockApi()
      .put('/v2/contests/contestJid/modules/config', {
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
          allowHigherDivisionsUnofficially: true,
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

    await user.click(screen.getByRole('button', { name: /save/i }));

    await waitFor(() => expect(nock.isDone()).toBe(true));
  });

  test('submits empty restriction when we allow all languages', async () => {
    await renderComponent({
      modules: [],
      config: {
        icpcStyle: {
          languageRestriction: { allowedLanguageNames: ['C', 'Pascal'] },
          wrongSubmissionPenalty: 20,
        },
        scoreboard: { isIncognitoScoreboard: false },
      },
    });

    const user = userEvent.setup();

    await user.click(await screen.findByRole('button', { name: /edit/i }));

    await user.click(screen.getByRole('checkbox', { name: /\(all\)/i }));

    nockApi()
      .put('/v2/contests/contestJid/modules/config', {
        icpcStyle: {
          languageRestriction: { allowedLanguageNames: [] },
          wrongSubmissionPenalty: 20,
        },
        scoreboard: { isIncognitoScoreboard: false },
      })
      .reply(200);

    await user.click(screen.getByRole('button', { name: /save/i }));

    await waitFor(() => expect(nock.isDone()).toBe(true));
  });

  test('submits the restriction when we allow not all languages', async () => {
    await renderComponent({
      modules: [],
      config: {
        icpcStyle: {
          languageRestriction: { allowedLanguageNames: [] },
          wrongSubmissionPenalty: 20,
        },
        scoreboard: { isIncognitoScoreboard: false },
      },
    });

    const user = userEvent.setup();

    await user.click(await screen.findByRole('button', { name: /edit/i }));

    await user.click(screen.getByRole('checkbox', { name: /\(all\)/i }));
    await user.click(screen.getByRole('checkbox', { name: /pascal/i }));
    await user.click(screen.getByRole('checkbox', { name: /python 3/i }));

    nockApi()
      .put('/v2/contests/contestJid/modules/config', {
        icpcStyle: {
          languageRestriction: { allowedLanguageNames: ['Pascal', 'Python3'] },
          wrongSubmissionPenalty: 20,
        },
        scoreboard: { isIncognitoScoreboard: false },
      })
      .reply(200);

    await user.click(screen.getByRole('button', { name: /save/i }));

    await waitFor(() => expect(nock.isDone()).toBe(true));
  });

  test('cannot enable or disable modules while editing', async () => {
    await renderComponent();

    const user = userEvent.setup();

    await user.click(await screen.findByRole('button', { name: /edit/i }));

    const toggles = screen.getAllByRole('button', { name: /^(enable|disable)$/i });
    expect(toggles).toHaveLength(12);
    toggles.forEach(button => expect(button).toBeDisabled());

    await user.click(screen.getByRole('button', { name: /cancel/i }));

    screen.getAllByRole('button', { name: /^(enable|disable)$/i }).forEach(button => expect(button).toBeEnabled());
  });

  test('enables a module', async () => {
    await renderComponent();

    const user = userEvent.setup();

    nockApi().put('/v2/contests/contestJid/modules/VIRTUAL').reply(200);

    const module = await findModule('Virtual contest');
    await user.click(within(module).getByRole('button', { name: /enable/i }));

    await waitFor(() => expect(nock.isDone()).toBe(true));
  });
});
