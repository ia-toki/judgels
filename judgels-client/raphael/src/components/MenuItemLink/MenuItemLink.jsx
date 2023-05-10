import { MenuItem } from '@blueprintjs/core';
import { push } from 'connected-react-router';
import { connect } from 'react-redux';

function MenuItemLink({ text, onClick }) {
  return <MenuItem text={text} onClick={onClick} />;
}

const mapDispatchToProps = {
  onClick: push,
};

const mergeProps = (stateProps, dispatchProps, { text, to }) => ({
  text,
  onClick: () => dispatchProps.onClick(to),
});

export default connect(undefined, mapDispatchToProps, mergeProps)(MenuItemLink);
