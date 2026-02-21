import { act, render, waitFor } from '@testing-library/react';
import nock from 'nock';

import { setSession } from '../../../../../../modules/session';
import { WebPrefsProvider } from '../../../../../../modules/webPrefs';
import { QueryClientProviderWrapper } from '../../../../../../test/QueryClientProviderWrapper';
import { TestRouter } from '../../../../../../test/RouterWrapper';
import { nockUriel } from '../../../../../../utils/nock';
import ContestEditorialPage from './ContestEditorialPage';

describe('ContestEditorialPage', () => {
  beforeEach(() => {
    setSession('token', { jid: 'userJid' });
  });

  afterEach(() => {
    nock.cleanAll();
  });

  const renderComponent = async () => {
    nockUriel().get('/contests/slug/contest-slug').reply(200, {
      jid: 'contestJid',
      slug: 'contest-slug',
    });

    nockUriel()
      .get('/contests/contestJid/editorial')
      .query({ language: 'en' })
      .reply(200, {
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
            type: 'Programming',
            defaultLanguage: 'id',
            titlesByLanguage: {
              id: 'Soal A',
            },
          },
          problemJid2: {
            slug: 'problem-b',
            type: 'Programming',
            defaultLanguage: 'id',
            titlesByLanguage: {
              id: 'Soal B',
              en: 'Problem B',
            },
          },
          problemJid3: {
            slug: 'problem-c',
            type: 'Programming',
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
      });

    await act(async () =>
      render(
        <WebPrefsProvider initialPrefs={{ editorialLanguage: 'en' }}>
          <QueryClientProviderWrapper>
            <TestRouter initialEntries={['/contests/contest-slug/editorial']} path="/contests/$contestSlug/editorial">
              <ContestEditorialPage />
            </TestRouter>
          </QueryClientProviderWrapper>
        </WebPrefsProvider>
      )
    );
  };

  describe('content', () => {
    beforeEach(async () => {
      await renderComponent();
    });

    it('shows the editorial', async () => {
      await waitFor(() => {
        expect(document.querySelector('.contest-editorial')).toBeInTheDocument();
      });
      expect(document.querySelector('.contest-editorial')).toHaveTextContent(
        '' +
          'Thanks for participating.' +
          'A. Soal AHello. This is editorial for problem A' +
          'B. Problem BHello. This is editorial for problem B'
      );
    });
  });
});
