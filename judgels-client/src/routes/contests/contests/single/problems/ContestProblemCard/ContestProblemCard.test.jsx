import { render, screen } from '@testing-library/react';
import { MemoryRouter } from 'react-router';

import { ContestProblemStatus } from '../../../../../../modules/api/uriel/contestProblem';
import { ContestProblemCard } from './ContestProblemCard';

describe('ContestProblemCard', () => {
  const renderComponent = problem => {
    const props = {
      contest: { jid: 'contestJid', slug: 'contest-a' },
      problem,
      problemName: 'The Problem',
      totalSubmissions: 10,
    };
    render(
      <MemoryRouter>
        <ContestProblemCard {...props} />
      </MemoryRouter>
    );
  };

  test('problem name', () => {
    renderComponent({
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

  test('problem name with points', () => {
    renderComponent({
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

  test('open problem with submissions limit', () => {
    renderComponent({
      problemJid: 'jid',
      alias: 'A',
      status: ContestProblemStatus.Open,
      submissionsLimit: 50,
      points: undefined,
    });

    const statusElement = document.querySelector('[data-key="status"]');
    expect(statusElement.textContent).toEqual('40 submissions left');
  });

  test('open problem without submissions limit', () => {
    renderComponent({
      problemJid: 'jid',
      alias: 'A',
      status: ContestProblemStatus.Open,
      submissionsLimit: undefined,
      points: undefined,
    });

    const statusElement = document.querySelector('[data-key="status"]');
    expect(statusElement.textContent).toEqual('');
  });

  test('closed problem', () => {
    renderComponent({
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
