import { mount, ReactWrapper } from 'enzyme';
import * as React from 'react';
import { IntlProvider } from 'react-intl';
import { Provider } from 'react-redux';
import { combineReducers, createStore } from 'redux';
import { reducer as formReducer } from 'redux-form';

import { contest } from 'fixtures/state';
import { ContestAnnouncement, ContestAnnouncementConfig } from 'modules/api/uriel/contestAnnouncement';

import { ContestAnnouncementsPage, ContestAnnouncementsPageProps } from './ContestAnnouncementsPage';
import { ContestAnnouncementCard } from '../ContestAnnouncementCard/ContestAnnouncementCard';

describe('ContestAnnouncementsPage', () => {
  let wrapper: ReactWrapper<any, any>;
  let onGetAnnouncements: jest.Mock<any>;
  let onCreateAnnouncement: jest.Mock<any>;
  let onUpdateAnnouncement: jest.Mock<any>;
  const config: ContestAnnouncementConfig = {
    isAllowedToCreateAnnouncement: true,
    isAllowedToEditAnnouncement: true,
  };

  const render = () => {
    const props: ContestAnnouncementsPageProps = {
      contest,
      onGetAnnouncements,
      onCreateAnnouncement,
      onUpdateAnnouncement,
    };

    const store = createStore(combineReducers({ form: formReducer }));

    wrapper = mount(
      <IntlProvider locale={navigator.language}>
        <Provider store={store}>
          <ContestAnnouncementsPage {...props} />
        </Provider>
      </IntlProvider>
    );
  };

  beforeEach(() => {
    onGetAnnouncements = jest.fn();
    onCreateAnnouncement = jest.fn();
  });

  describe('when there are no announcements', () => {
    beforeEach(() => {
      onGetAnnouncements.mockReturnValue(Promise.resolve({ data: [], config }));
      render();
    });

    it('shows placeholder text and no announcements', async () => {
      await new Promise(resolve => setImmediate(resolve));
      wrapper.update();

      expect(wrapper.text()).toContain('No announcements.');
      expect(wrapper.find(ContestAnnouncementCard)).toHaveLength(0);
    });
  });

  describe('when there are announcements', () => {
    beforeEach(() => {
      const announcements: ContestAnnouncement[] = [
        {
          jid: 'jid1',
          userJid: 'userJid1',
          title: 'title1',
          content: 'content1',
          updatedTime: 0,
        } as ContestAnnouncement,
        {
          jid: 'jid2',
          userJid: 'userJid2',
          title: 'title2',
          content: 'content2',
          updatedTime: 0,
        } as ContestAnnouncement,
      ];
      onGetAnnouncements.mockReturnValue(Promise.resolve({ data: announcements, config }));

      render();
    });

    it('shows the announcements', async () => {
      await new Promise(resolve => setImmediate(resolve));
      wrapper.update();

      expect(wrapper.find(ContestAnnouncementCard)).toHaveLength(2);
    });
  });
});
