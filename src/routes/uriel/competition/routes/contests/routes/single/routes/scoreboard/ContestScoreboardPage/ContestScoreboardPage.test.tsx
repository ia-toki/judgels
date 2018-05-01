import { mount, ReactWrapper } from 'enzyme';
import * as React from 'react';
import { IntlProvider } from 'react-intl';

import { contest } from '../../../../../../../../../../fixtures/state';
import { ContestScoreboardPage, ContestScoreboardPageProps } from './ContestScoreboardPage';
import { IcpcScoreboardTable } from '../IcpcScoreboardTable/IcpcScoreboardTable';
import { ContestScoreboardResponse } from '../../../../../../../../../../modules/api/uriel/contestScoreboard';
import { ContestScoreboardType } from '../../../../../../../../../../modules/api/uriel/contestScoreboard';

describe('ContestScoreboardPage', () => {
  let wrapper: ReactWrapper<any, any>;
  let onFetchScoreboard: jest.Mock<any>;

  const render = () => {
    const props: ContestScoreboardPageProps = {
      contest,
      onFetchScoreboard,
    };

    wrapper = mount(
      <IntlProvider locale={navigator.language}>
        <ContestScoreboardPage {...props} />
      </IntlProvider>
    );
  };

  beforeEach(() => {
    onFetchScoreboard = jest.fn();
  });

  describe('when there is no scoreboard', () => {
    beforeEach(() => {
      onFetchScoreboard.mockReturnValue(Promise.resolve(null));
      render();
    });

    it('shows placeholder text and no scoreboard', async () => {
      await new Promise(resolve => setImmediate(resolve));
      wrapper.update();

      expect(wrapper.text()).toContain('No scoreboard.');
      expect(wrapper.find(IcpcScoreboardTable)).toHaveLength(0);
    });
  });

  describe('when there is scoreboard', () => {
    beforeEach(() => {
      const response: ContestScoreboardResponse = {
        data: {
          type: ContestScoreboardType.Official,
          scoreboard: {
            state: {
              contestantJids: [],
              problemJids: [],
              problemAliases: [],
            },
            content: {
              entries: [],
            },
          },
          updatedTime: 0,
        },
        usersMap: {},
      };
      onFetchScoreboard.mockReturnValue(Promise.resolve(response));

      render();
    });

    it('shows the scoreboard', async () => {
      await new Promise(resolve => setImmediate(resolve));
      wrapper.update();

      expect(wrapper.find(IcpcScoreboardTable)).toHaveLength(1);
    });
  });
});
