import { render, screen, within } from '@testing-library/react';
import { MemoryRouter } from 'react-router';

import { ContestContestantsTable } from './ContestContestantsTable';

describe('ContestContestantsTable', () => {
  let virtualModuleConfig;
  let contestants;
  let now;

  const renderComponent = () => {
    const props = {
      contest: {
        beginTime: 10,
        duration: 100,
      },
      virtualModuleConfig,
      contestants,
      profilesMap: {
        userJid1: { username: 'userC' },
        userJid2: { username: 'userD' },
        userJid3: { username: 'userA' },
        userJid4: { username: 'userE' },
        userJid5: { username: 'userB' },
      },
      now,
    };

    render(
      <MemoryRouter>
        <ContestContestantsTable {...props} />
      </MemoryRouter>
    );
  };

  describe('when contest is not virtual', () => {
    beforeEach(() => {
      contestants = [
        { userJid: 'userJid1' },
        { userJid: 'userJid2' },
        { userJid: 'userJid3' },
        { userJid: 'userJid4' },
        { userJid: 'userJid5' },
      ];
      renderComponent();
    });

    it('shows the correct columns', () => {
      const rows = screen.getAllByRole('row').slice(1);
      const usernames = rows.map(row => row.children[1].textContent);
      expect(usernames).toEqual(['userA', 'userB', 'userC', 'userD', 'userE']);
    });
  });

  describe('when contest is virtual', () => {
    beforeEach(() => {
      now = 70;
      virtualModuleConfig = { virtualDuration: 50 };
      contestants = [
        { userJid: 'userJid1' },
        { userJid: 'userJid2', contestStartTime: 30 },
        { userJid: 'userJid3', contestStartTime: 20 },
        { userJid: 'userJid4', contestStartTime: 65 },
        { userJid: 'userJid5' },
      ];
      renderComponent();
    });

    it('shows the correct columns', () => {
      const rows = screen.getAllByRole('row').slice(1);
      expect(rows).toHaveLength(5);

      expect(rows[0].children[1]).toHaveTextContent('userA');
      expect(within(rows[0]).getByRole('progressbar')).toBeInTheDocument();

      expect(rows[1].children[1]).toHaveTextContent('userD');
      expect(within(rows[1]).getByRole('progressbar')).toBeInTheDocument();

      expect(rows[2].children[1]).toHaveTextContent('userE');
      expect(within(rows[2]).getByRole('progressbar')).toBeInTheDocument();

      expect(rows[3].children[1]).toHaveTextContent('userB');
      expect(rows[4].children[1]).toHaveTextContent('userC');
    });
  });
});
