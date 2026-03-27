import { act, render, screen, waitFor, within } from '@testing-library/react';

import { setSession } from '../../../../../../modules/session';
import { WebPrefsProvider } from '../../../../../../modules/webPrefs';
import { QueryClientProviderWrapper } from '../../../../../../test/QueryClientProviderWrapper';
import { TestRouter } from '../../../../../../test/RouterWrapper';
import { nockUriel } from '../../../../../../utils/nock';
import ContestClarificationsPage from './ContestClarificationsPage';

describe('ContestClarificationsPage', () => {
  beforeEach(() => {
    setSession('token', { jid: 'userJid' });
  });

  const renderComponent = async ({
    clarifications = [
      {
        jid: 'clarificationJid1',
        userJid: 'userJid1',
        topicJid: 'contestJid',
        title: 'Title 1',
        question: 'Question 1',
        status: 'ANSWERED',
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
        status: 'ASKED',
        time: new Date(new Date().setDate(new Date().getDate() - 1)).getTime(),
      },
    ],
    canCreate,
    canSupervise,
  } = {}) => {
    nockUriel().get('/contests/slug/contest-slug').reply(200, {
      jid: 'contestJid',
      slug: 'contest-slug',
    });

    nockUriel()
      .get('/contests/contestJid/clarifications')
      .query({ language: 'en' })
      .reply(200, {
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
      });

    await act(async () =>
      render(
        <WebPrefsProvider initialPrefs={{ statementLanguage: 'en' }}>
          <QueryClientProviderWrapper>
            <TestRouter
              initialEntries={['/contests/contest-slug/clarifications']}
              path="/contests/$contestSlug/clarifications"
            >
              <ContestClarificationsPage />
            </TestRouter>
          </QueryClientProviderWrapper>
        </WebPrefsProvider>
      )
    );
  };

  test('renders no action buttons when not canCreate', async () => {
    await renderComponent({ canCreate: false });
    await screen.findByRole('heading', { name: 'Clarifications' });
    expect(screen.queryByRole('button', { name: /new announcement/i })).not.toBeInTheDocument();
  });

  test('renders action buttons when canCreate', async () => {
    await renderComponent({ canCreate: true });
    expect(await screen.findByRole('button', { name: /new clarification/i })).toBeInTheDocument();
  });

  test('renders placeholder when there are no clarifications', async () => {
    await renderComponent({ clarifications: [] });
    expect(await screen.findByText('No clarifications.')).toBeInTheDocument();
    expect(screen.queryAllByRole('article')).toHaveLength(0);
  });

  test('renders clarifications when not canSupervise', async () => {
    await renderComponent({ canSupervise: false });
    await waitFor(() => {
      expect(screen.getAllByRole('article').length).toBeGreaterThan(0);
    });
    const clarifications = screen.getAllByRole('article');
    expect(clarifications).toHaveLength(4);

    expect(within(clarifications[0]).getAllByRole('heading')[0]).toHaveTextContent('Title 1 General');
    expect(within(clarifications[0]).getByText('Question 1')).toBeInTheDocument();
    expect(clarifications[0].querySelector('small')).toHaveTextContent(/asked 2 days ago$/);

    expect(within(clarifications[1]).getByRole('heading')).toHaveTextContent('Answer:');
    expect(within(clarifications[1]).getByText('Answer 1')).toBeInTheDocument();
    expect(clarifications[1].querySelector('small')).toHaveTextContent(/answered 1 day ago$/);

    expect(within(clarifications[2]).getAllByRole('heading')[0]).toHaveTextContent('Title 2 A. Problem 1');
    expect(within(clarifications[2]).getByText('Question 2')).toBeInTheDocument();
    expect(clarifications[2].querySelector('small')).toHaveTextContent(/asked 1 day ago$/);

    expect(within(clarifications[3]).queryByRole('heading')).not.toBeInTheDocument();
  });

  test('renders clarifications when canSupervise', async () => {
    await renderComponent({ canSupervise: true });
    await waitFor(() => {
      expect(screen.getAllByRole('article').length).toBeGreaterThan(0);
    });
    const clarifications = screen.getAllByRole('article');
    expect(clarifications).toHaveLength(4);

    expect(within(clarifications[0]).getAllByRole('heading')[0]).toHaveTextContent('Title 1 General');
    expect(within(clarifications[0]).getByText('Question 1')).toBeInTheDocument();
    expect(clarifications[0].querySelector('small')).toHaveTextContent(/asked 2 days ago by username1$/);

    expect(within(clarifications[1]).getByRole('heading')).toHaveTextContent('Answer:');
    expect(within(clarifications[1]).getByText('Answer 1')).toBeInTheDocument();
    expect(clarifications[1].querySelector('small')).toHaveTextContent(/answered 1 day ago by username3$/);

    expect(within(clarifications[2]).getAllByRole('heading')[0]).toHaveTextContent('Title 2 A. Problem 1');
    expect(within(clarifications[2]).getByText('Question 2')).toBeInTheDocument();
    expect(clarifications[2].querySelector('small')).toHaveTextContent(/asked 1 day ago by username2$/);

    expect(within(clarifications[3]).queryByRole('heading')).not.toBeInTheDocument();
  });
});
