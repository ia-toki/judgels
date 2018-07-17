import { MenuItem } from '@blueprintjs/core';
import * as React from 'react';
import { push } from 'react-router-redux';
import { connect } from 'react-redux';

export interface MenuItemLinkProps {
  text: string;
  to: string;
}

interface MenuItemLinkConnectedProps {
  text: string;
  onClick: () => void;
}

const MenuItemLink = (props: MenuItemLinkConnectedProps) => <MenuItem text={props.text} onClick={props.onClick} />;

const mapDispatchToProps = dispatch => ({
  onClick: (to: string) => dispatch(push(to)),
});

const mergeProps = (stateProps, dispatchProps, ownProps: MenuItemLinkProps) => ({
  text: ownProps.text,
  onClick: () => dispatchProps.onClick(ownProps.to),
});

export default connect(undefined, mapDispatchToProps, mergeProps)(MenuItemLink);
