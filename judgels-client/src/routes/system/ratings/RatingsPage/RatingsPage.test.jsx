import { mount } from 'enzyme';
import { Provider } from 'react-redux';
import { MemoryRouter } from 'react-router';
import { applyMiddleware, createStore } from 'redux';
import thunk from 'redux-thunk';

import RatingsPage from './RatingsPage';

import * as ratingActions from '../modules/ratingActions';

jest.mock('../modules/ratingActions');

describe('RatingsPage', () => {
  let wrapper;
  let contests;
  let ratingChangesMap;

  const render = async () => {
    ratingActions.getContestsPendingRating.mockReturnValue(() =>
      Promise.resolve({
        data: contests,
        ratingChangesMap,
      })
    );
    ratingActions.updateRatings.mockReturnValue(() => Promise.resolve());

    const store = createStore(() => {}, applyMiddleware(thunk));

    wrapper = mount(
      <Provider store={store}>
        <MemoryRouter>
          <RatingsPage />
        </MemoryRouter>
      </Provider>
    );

    await new Promise(resolve => setImmediate(resolve));
    wrapper.update();
  };

  describe('when there are no contests pending ratings', () => {
    beforeEach(async () => {
      contests = [];
      await render();
    });

    it('shows placeholder text and no files', async () => {
      expect(wrapper.text()).toContain('No contests.');
    });
  });

  describe('when there are contests', () => {
    beforeEach(async () => {
      contests = [
        {
          jid: 'contestJid1',
          name: 'Contest 1',
          beginTime: 100,
          duration: 50,
        },
        {
          jid: 'contestJid2',
          name: 'Contest 2',
          beginTime: 200,
          duration: 100,
        },
      ];
      ratingChangesMap = {
        contestJid1: {
          ratingsMap: {
            userJid1: { publicRating: 1600, hiddenRating: 1500 },
            userJid2: { publicRating: 1700, hiddenRating: 1400 },
          },
          profilesMap: {
            userJid1: { username: 'user1' },
            userJid2: { username: 'user2' },
          },
        },
        contestJid2: {
          ratingsMap: {
            userJid1: { publicRating: 1500, hiddenRating: 1400 },
          },
          profilesMap: {
            userJid1: { username: 'user1' },
          },
        },
      };
      await render();
    });

    it('shows the contests', () => {
      expect(wrapper.find('tr').map(tr => tr.find('td').map(td => td.text()))).toEqual([
        [],
        ['Contest 1', 'View rating changes'],
        ['Contest 2', 'View rating changes'],
      ]);
    });

    describe('when view rating changes button is clicked', () => {
      beforeEach(() => {
        wrapper.find('tr').at(1).find('td').at(1).find('button').simulate('click');
        wrapper.update();
      });

      it('shows the users with rating changes', () => {
        expect(
          wrapper
            .find('.contest-rating-changes-dialog')
            .find('tr')
            .map(tr => tr.find('td').map(td => td.text()))
        ).toEqual([[], ['user2', '1700'], ['user1', '1600']]);
      });

      describe('when apply rating changes button is clicked', () => {
        beforeEach(() => {
          wrapper.find('.contest-rating-changes-dialog').find('button').last().simulate('click');
          wrapper.update();
        });

        it('calls API', () => {
          expect(ratingActions.updateRatings).toHaveBeenCalledWith({
            eventJid: 'contestJid1',
            time: 150,
            ratingsMap: ratingChangesMap.contestJid1.ratingsMap,
          });
        });
      });
    });
  });
});
