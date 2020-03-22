//
// Created by Tri Nguyen on 09/02/2020.
//

#include <stdbool.h>

#ifndef BAHN_DATA_UTIL_H
#define BAHN_DATA_UTIL_H

bool initialise_config(const char *config_dir);

void free_config();

int interlocking_table_get_routes(const char *src_signal_id, const char *dst_signal_id, char *route_ids[]);

char *config_get_scalar_string_value(const char *type, const char *id, const char *prop_name);

int config_get_scalar_int_value(const char *type, const char *id, const char *prop_name);

float config_get_scalar_float_value(const char *type, const char *id, const char *prop_name);

bool config_get_scalar_bool_value(const char *type, const char *id, const char *prop_name);

int config_get_array_string_value(const char *type, const char *id, const char *prop_name, char* data[]);

int config_get_array_int_value(const char *type, const char *id, const char *prop_name, int data[]);

int config_get_array_float_value(const char *type, const char *id, const char *prop_name, float data[]);

int config_get_array_bool_value(const char *type, const char *id, const char *prop_name, bool data[]);

bool config_set_scalar_string_value(const char *type, const char *id, const char *prop_name, char *value);

char *track_state_get_value(const char *type, const char *id);

bool track_state_set_value(const char *type, const char *id, const char *value);

bool is_segment_occupied(const char *id);

char *config_get_point_position(const char *route_id, const char *point_id);

bool string_equals(const char *str1, const char *str2);

void init_cached_track_state();

void free_cached_track_state();

#endif //BAHN_DATA_UTIL_H