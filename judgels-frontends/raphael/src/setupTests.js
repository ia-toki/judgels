import { configure } from 'enzyme';
import Adapter from 'enzyme-adapter-preact-pure';
import ReactDOM from 'react-dom';
import nock from 'nock';

configure({ adapter: new Adapter() });

nock.disableNetConnect();

ReactDOM.createPortal = el => el;

window.conf = {
  name: 'Judgels',
  slogan: 'Judgment Angels',
  apiUrls: {
    jophiel: 'http://jophiel',
    uriel: 'http://uriel',
    jerahmeel: 'http://jerahmeel',
  },
  welcomeBanner: {
    title: 'Welcome to Judgels',
    description:
      'Lorem ipsum dolor sit amet, consectetur adipisicing elit.Lorem ipsum dolor sit amet, consectetur adipisicing elit. Lorem ipsum dolor sit amet, consectetur adipisicing elit. Lorem ipsum dolor sit amet, consectetur adipisicing elit.',
  },
};

window.scrollTo = function() {
  return;
};
