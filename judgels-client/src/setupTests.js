import { configure } from 'enzyme';
import Adapter from 'enzyme-adapter-preact-pure';
import nock from 'nock';
import ReactDOM from 'react-dom';

configure({ adapter: new Adapter() });

nock.disableNetConnect();

ReactDOM.createPortal = el => el;

window.conf = {
  mode: 'TLX',
  name: 'Judgels',
  slogan: 'Judgment Angels',
  apiUrl: 'http://api',
  welcomeBanner: {
    title: 'Welcome to Judgels',
    description:
      'Lorem ipsum dolor sit amet, consectetur adipisicing elit.Lorem ipsum dolor sit amet, consectetur adipisicing elit. Lorem ipsum dolor sit amet, consectetur adipisicing elit. Lorem ipsum dolor sit amet, consectetur adipisicing elit.',
  },
};

window.scrollTo = function () {
  return;
};
