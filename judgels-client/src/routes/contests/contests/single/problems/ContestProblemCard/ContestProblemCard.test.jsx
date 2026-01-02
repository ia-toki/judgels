import { act, render, screen } from '@testing-library/react';

import { ContestProblemStatus } from '../../../../../../modules/api/uriel/contestProblem';
import { TestRouter } from '../../../../../../test/RouterWrapper';
import { ContestProblemCard } from './ContestProblemCard';

describe('ContestProblemCard', () => {
  const renderComponent = async problem => {
    const props = {
      contest: { jid: 'contestJid', slug: 'contest-a' },
      problem,
      problemName: 'The Problem',
      totalSubmissions: 10,
    };
    await act(async () =>
      render(
        <TestRouter>
          <ContestProblemCard {...props} />
        </TestRouter>
      )
    );
  };

  test('problem name', async () => {
    await renderComponent({
      problemJid: 'jid',
      alias: 'A',
      status: ContestProblemStatus.Open,
      submissionsLimit: 50,
      points: undefined,
    });

    const nameElement = document.querySelector('[data-key="name"]');
    expect(nameElement.textContent).toEqual('A. The Problem');

    const linkElement = screen.getByRole('link');
    expect(linkElement.getAttribute('href')).toEqual('/contests/contest-a/problems/A');
  });

  test('problem name with points', async () => {
    await renderComponent({
      problemJid: 'jid',
      alias: 'A',
      status: ContestProblemStatus.Open,
      submissionsLimit: 50,
      points: 30,
    });

    const nameElement = document.querySelector('[data-key="name"]');
    expect(nameElement.textContent).toEqual('A. The Problem [30 points]');

    const linkElement = screen.getByRole('link');
    expect(linkElement.getAttribute('href')).toEqual('/contests/contest-a/problems/A');
  });

  test('open problem with submissions limit', async () => {
    await renderComponent({
      problemJid: 'jid',
      alias: 'A',
      status: ContestProblemStatus.Open,
      submissionsLimit: 50,
      points: undefined,
    });

    const statusElement = document.querySelector('[data-key="status"]');
    expect(statusElement.textContent).toEqual('40 submissions left');
  });

  test('open problem without submissions limit', async () => {
    await renderComponent({
      problemJid: 'jid',
      alias: 'A',
      status: ContestProblemStatus.Open,
      submissionsLimit: undefined,
      points: undefined,
    });

    const statusElement = document.querySelector('[data-key="status"]');
    expect(statusElement.textContent).toEqual('');
  });

  test('closed problem', async () => {
    await renderComponent({
      problemJid: 'jid',
      alias: 'A',
      status: ContestProblemStatus.Closed,
      submissionsLimit: 50,
      points: undefined,
    });

    const statusElement = document.querySelector('[data-key="status"]');
    expect(statusElement.textContent).toEqual('CLOSED');
  });
});
