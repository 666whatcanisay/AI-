<template>
  <router-view />
</template>

<script setup lang="ts">
</script>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  font-family: 'Microsoft YaHei', 'PingFang SC', sans-serif;
}

.action-buttons {
  display: flex;
  flex-wrap: nowrap;
  gap: 6px;
  align-items: center;
  justify-content: flex-start;
  white-space: nowrap;
}

.el-button {
  border-radius: 8px !important;
  font-weight: 500;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  border-width: 1px;
}

.el-button:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.el-button:active {
  transform: translateY(0);
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.1);
}

.el-button--primary {
  background: linear-gradient(135deg, #DC2626 0%, #B91C1C 100%) !important;
  border-color: #DC2626 !important;
  box-shadow: 0 2px 8px rgba(220, 38, 38, 0.3);
}

.el-button--primary:hover {
  background: linear-gradient(135deg, #EF4444 0%, #DC2626 100%) !important;
  border-color: #EF4444 !important;
  box-shadow: 0 4px 16px rgba(220, 38, 38, 0.4);
}

.el-button--primary.is-plain {
  background: #fef2f2 !important;
  border-color: #fecaca !important;
  color: #DC2626 !important;
}

.el-button--primary.is-plain:hover {
  background: #fee2e2 !important;
  border-color: #fca5a5 !important;
  color: #B91C1C !important;
}

.el-button--success {
  background: linear-gradient(135deg, #059669 0%, #047857 100%) !important;
  border-color: #059669 !important;
  box-shadow: 0 2px 8px rgba(5, 150, 105, 0.3);
}

.el-button--success:hover {
  background: linear-gradient(135deg, #10B981 0%, #059669 100%) !important;
  border-color: #10B981 !important;
  box-shadow: 0 4px 16px rgba(16, 185, 129, 0.4);
}

.el-button--success.is-plain {
  background: #ecfdf5 !important;
  border-color: #a7f3d0 !important;
  color: #047857 !important;
}

.el-button--success.is-plain:hover {
  background: #d1fae5 !important;
  border-color: #6ee7b7 !important;
  color: #065F46 !important;
}

.el-button--warning {
  background: linear-gradient(135deg, #D97706 0%, #B45309 100%) !important;
  border-color: #D97706 !important;
  box-shadow: 0 2px 8px rgba(217, 119, 6, 0.3);
}

.el-button--warning:hover {
  background: linear-gradient(135deg, #F59E0B 0%, #D97706 100%) !important;
  border-color: #F59E0B !important;
  box-shadow: 0 4px 16px rgba(245, 158, 11, 0.4);
}

.el-button--warning.is-plain {
  background: #fffbeb !important;
  border-color: #fde68a !important;
  color: #B45309 !important;
}

.el-button--warning.is-plain:hover {
  background: #fef3c7 !important;
  border-color: #fcd34d !important;
  color: #92400E !important;
}

.el-button--danger {
  background: linear-gradient(135deg, #DC2626 0%, #B91C1C 100%) !important;
  border-color: #DC2626 !important;
  box-shadow: 0 2px 8px rgba(220, 38, 38, 0.3);
}

.el-button--danger:hover {
  background: linear-gradient(135deg, #EF4444 0%, #DC2626 100%) !important;
  border-color: #EF4444 !important;
  box-shadow: 0 4px 16px rgba(239, 68, 68, 0.4);
}

.el-button--danger.is-plain {
  background: #fef2f2 !important;
  border-color: #fecaca !important;
  color: #DC2626 !important;
}

.el-button--danger.is-plain:hover {
  background: #fee2e2 !important;
  border-color: #fca5a5 !important;
  color: #991B1B !important;
}

.el-button--info {
  background: linear-gradient(135deg, #6B7280 0%, #4B5563 100%) !important;
  border-color: #6B7280 !important;
  box-shadow: 0 2px 8px rgba(107, 114, 128, 0.3);
}

.el-button--info:hover {
  background: linear-gradient(135deg, #9CA3AF 0%, #6B7280 100%) !important;
  border-color: #9CA3AF !important;
  box-shadow: 0 4px 16px rgba(107, 114, 128, 0.4);
}

.el-button--info.is-plain {
  background: #f9fafb !important;
  border-color: #e5e7eb !important;
  color: #4B5563 !important;
}

.el-button--info.is-plain:hover {
  background: #f3f4f6 !important;
  border-color: #d1d5db !important;
  color: #374151 !important;
}

.el-button--default {
  background: #ffffff !important;
  border-color: #e5e7eb !important;
  color: #374151 !important;
}

.el-button--default:hover {
  background: #f9fafb !important;
  border-color: #d1d5db !important;
  color: #1f2937 !important;
}

.el-button--small {
  padding: 8px 14px !important;
  font-size: 12px;
}

.el-button--medium {
  padding: 10px 18px !important;
  font-size: 14px;
}

.el-button--large {
  padding: 12px 24px !important;
  font-size: 16px;
}

.el-button.is-text {
  background: transparent !important;
  border-color: transparent !important;
  box-shadow: none !important;
}

.el-button.is-text:hover {
  background: rgba(220, 38, 38, 0.08) !important;
  transform: none !important;
  box-shadow: none !important;
}

.el-button.is-text.el-button--primary {
  color: #DC2626 !important;
}

.el-button.is-link {
  background: transparent !important;
  border-color: transparent !important;
  box-shadow: none !important;
}

.el-button.is-link:hover {
  transform: none !important;
  box-shadow: none !important;
}

.el-button.is-link.el-button--primary {
  color: #DC2626 !important;
}

.el-button.is-link.el-button--primary:hover {
  color: #B91C1C !important;
  background: transparent !important;
}

.el-card {
  border-radius: 12px !important;
  border: none !important;
  box-shadow: 0 4px 16px rgba(127, 29, 29, 0.08) !important;
}

.el-card:hover {
  box-shadow: 0 8px 24px rgba(127, 29, 29, 0.12) !important;
}

.el-table {
  border-radius: 12px !important;
  overflow: hidden;
}

.el-table th {
  background-color: #fef2f2 !important;
  color: #7f1d1d !important;
  font-weight: 600 !important;
}

.el-table tr:hover > td {
  background-color: #fff7ed !important;
}

.el-pagination {
  color: #7f1d1d;
}

.el-pagination.is-background .el-pager li:not(.is-disabled).is-active {
  background-color: #DC2626 !important;
  color: #ffffff !important;
}

.el-pagination.is-background .el-pager li:not(.is-disabled):hover {
  color: #DC2626 !important;
}

.el-dialog {
  border-radius: 16px !important;
}

.el-dialog__header {
  background: linear-gradient(135deg, #fef2f2 0%, #ffffff 100%);
  border-bottom: 2px solid #fecaca;
  padding: 16px 20px !important;
}

.el-dialog__title {
  color: #7f1d1d !important;
  font-weight: 600 !important;
}

.el-dialog__footer .el-button {
  min-width: 80px;
}

.el-message-box__btns .el-button {
  min-width: 80px;
}

.el-tag {
  border-radius: 6px !important;
}

.el-input__wrapper {
  border-radius: 8px !important;
  transition: all 0.3s ease;
}

.el-input__wrapper:hover {
  box-shadow: 0 0 0 1px #DC2626 !important;
}

.el-input__wrapper.is-focus {
  box-shadow: 0 0 0 1px #DC2626 !important;
}

.el-select .el-input__wrapper {
  border-radius: 8px !important;
}

.el-dropdown-menu {
  border-radius: 12px !important;
  box-shadow: 0 8px 24px rgba(127, 29, 29, 0.15) !important;
}

.el-dropdown-menu__item:hover {
  background-color: #fef2f2 !important;
  color: #DC2626 !important;
}
</style>
